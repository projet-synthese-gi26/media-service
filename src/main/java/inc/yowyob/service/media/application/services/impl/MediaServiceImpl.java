package inc.yowyob.service.media.application.services.impl;

import inc.yowyob.service.media.api.exceptions.FileNotFoundException;
import inc.yowyob.service.media.application.services.MediaService;
import inc.yowyob.service.media.application.services.MinioService;
import inc.yowyob.service.media.infrastructure.persistence.entities.Media;
import inc.yowyob.service.media.infrastructure.persistence.repositories.MediaRepository;
import inc.yowyob.service.media.utils.FileUtils;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final MinioService minioService;

    private final MediaRepository mediaRepository;

    @Value("${app.public-url:http://localhost:8081}")
    private String publicUrl;

    @Override
    public Mono<Media> uploadMedia(FilePart filePart, String service, String location) {
        String realFileName = filePart.filename();
        String fileName = FileUtils.generateHashedFileName(realFileName);
        String extension = FileUtils.getExtension(fileName);

        String normalizedLocation = location.replace("\\", "/");
        String objectKey = Path.of(normalizedLocation, fileName).toString().replace("\\", "/");
        String realService = FileUtils.sanitizeService(service);

        return minioService.upload(Path.of(objectKey), realService, filePart).flatMap(uploadedPath -> {
            Media media = new Media();
            media.setService(realService);
            media.setName(fileName);
            media.setRealName(realFileName);
            media.setSize(filePart.headers().getContentLength());
            media.setMime(FileUtils.getContentType(filePart));
            media.setExtension(extension);
            media.setPath(objectKey);

            media.setUri(Path.of(media.getService(), objectKey).toString().replace("\\", "/"));

            return mediaRepository.save(media)
                    .map(savedMedia -> {
                        String accessUrl = String.format("%s/media/%s",
                                removeTrailingSlash(publicUrl),
                                savedMedia.getId());

                        savedMedia.setUri(accessUrl);
                        log.info("Media saved with ID: {} and Public URL: {}", savedMedia.getId(), accessUrl);
                        return savedMedia;
                    });
        });
    }

    @Override
    public Flux<Media> uploadMultipleMedia(Flux<FilePart> fileParts, String service, String location) {
        return fileParts.flatMapSequential(filePart -> uploadMedia(filePart, service, location));
    }

    @Override
    public Mono<Media> replaceMedia(UUID id, FilePart filePart, String location) {
        // Même logique pour le replace
        String realFileName = filePart.filename();
        String fileName = FileUtils.generateHashedFileName(realFileName);
        String extension = FileUtils.getExtension(fileName);

        return this.getMediaMetadata(id).flatMap(media -> {
            String oldPath = media.getPath();
            Path source = Path.of(Path.of(media.getPath()).getParent().toString(), fileName);
            if (location != null && !location.trim().isEmpty()) {
                source = Path.of(location, fileName);
            }

            return minioService.upload(source, media.getService(), filePart).flatMap(objectKey -> {
                media.setName(fileName);
                media.setRealName(realFileName);
                media.setSize(filePart.headers().getContentLength());
                media.setMime(FileUtils.getContentType(filePart));
                media.setExtension(extension);
                media.setPath(objectKey.toString());

                return mediaRepository.save(media);
            }).flatMap(newMedia -> {
                return minioService.remove(Path.of(oldPath), media.getService())
                        .then(Mono.fromCallable(() -> {
                            String accessUrl = String.format("%s/media/%s",
                                    removeTrailingSlash(publicUrl),
                                    newMedia.getId());
                            newMedia.setUri(accessUrl);
                            return newMedia;
                        }))
                        .onErrorResume(err -> {
                            String accessUrl = String.format("%s/media/%s",
                                    removeTrailingSlash(publicUrl),
                                    newMedia.getId());
                            newMedia.setUri(accessUrl);
                            return Mono.just(newMedia);
                        });
            });
        });
    }

    @SneakyThrows
    @Override
    public Mono<byte[]> downloadMedia(UUID id) {
        return this.getMediaMetadata(id)
                .doOnNext(media -> log.info("Downloading media: id={}, path={}, service={}",
                        id, media.getPath(), media.getService()))
                .flatMap(media -> minioService.get(Path.of(media.getPath()), media.getService()))
                .doOnNext(inputStream -> log.info("File found and stream obtained"))
                .map(inputStream -> {
                    try {
                        return inputStream.readAllBytes();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .doOnError(err -> log.error("Error downloading media: {}", err.getMessage()));
    }

    @Override
    public Mono<String> getMediaUrl(UUID id, int expiryInSeconds) {
        return this.getMediaMetadata(id).flatMap(media -> {
            return minioService.generatePresignedUrl(media.getService(), media.getPath(), expiryInSeconds);
        });
    }

    private String removeTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    @Override
    public Mono<Media> getMediaMetadata(UUID id) {
        return mediaRepository.findById(id)
                .switchIfEmpty(Mono.error(new FileNotFoundException(id.toString())));
    }

    @Override
    public Flux<Media> listMedia(String service) {
        return mediaRepository.findByService(service);
    }

    @Override
    public Mono<Void> deleteMedia(UUID id) {
        return this.getMediaMetadata(id).flatMap(media -> {
            return mediaRepository.delete(media).doOnTerminate(() -> {
                minioService.remove(Path.of(media.getPath(), media.getService()));
            });
        });
    }

    @Override
    public Mono<Void> deleteMediaByPath(String path) {
        return mediaRepository.findByPath(path).flatMap(media -> {
            Mono<Void> removeMediaMono = minioService.remove(Path.of(media.getPath(), media.getService())).onErrorResume(err -> {
                log.error("Minio error", err);
                return Mono.empty();
            });
            return mediaRepository.delete(media).then(removeMediaMono);
        });
    }

    @Override
    public Flux<Media> searchMediaByName(String service, String name) {
        return mediaRepository.findByServiceAndNameContainingIgnoreCase(service, name);
    }

    @Override
    public Mono<Media> findByPath(String service, String path) {
        return mediaRepository.findByServiceAndPath(service, path);
    }
}

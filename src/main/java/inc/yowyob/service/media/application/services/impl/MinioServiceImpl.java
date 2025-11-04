package inc.yowyob.service.media.application.services.impl;


import inc.yowyob.service.media.api.exceptions.BucketNotFoundException;
import inc.yowyob.service.media.api.exceptions.MinioException;
import inc.yowyob.service.media.api.exceptions.MinioFetchException;
import inc.yowyob.service.media.application.services.MinioService;
import inc.yowyob.service.media.infrastructure.properties.MinioConfigurationProperties;
import inc.yowyob.service.media.utils.FileUtils;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@RequiredArgsConstructor
@Service
@Slf4j
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;

    private final MinioAsyncClient minioAsyncClient;


    private final MinioConfigurationProperties configurationProperties;

    @Override
    public Mono<Boolean> isBucketExist(String bucket) {
        try {
            return Mono.fromFuture(
                    minioAsyncClient.bucketExists(this.makeBucketExists(bucket))
            );
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    private Mono<Boolean> requireBucketOrCreate(String bucket, boolean createIfNotExists) {
        return this.isBucketExist(bucket).flatMap(isBucketExist -> {
            log.info("------------------------------------- is pucket exsts {}", isBucketExist);
            if (!isBucketExist && !createIfNotExists) {
                return Mono.error(new BucketNotFoundException(bucket));
            } else if (!isBucketExist) {
                return createBucket(bucket).thenReturn(true);
            }
            return Mono.just(true);
        });
    }

    private MakeBucketArgs makeBucketArgs(String bucket) {
        return MakeBucketArgs.builder().bucket(bucket).build();
    }

    private BucketExistsArgs makeBucketExists(String bucket) {
        return BucketExistsArgs.builder().bucket(bucket).build();
    }

    private PutObjectArgs makePutObjectArgs(String bucket, String source, byte[] bytes, MediaType contentType, Map<String, String> headers) {

        PutObjectArgs.Builder builder = PutObjectArgs.builder().bucket(bucket);
        builder.object(source);
        builder.stream(new ByteArrayInputStream(bytes), bytes.length, -1);

        if (headers != null && !headers.isEmpty()) {
            builder.headers(headers);
        }

        if (contentType != null) {
            builder.contentType(contentType.toString());
        }

        return builder.build();
    }

    private PutObjectArgs makePutObjectArgs(String bucket, String source, byte[] bytes, MediaType contentType) {
        return this.makePutObjectArgs(bucket, source, bytes, contentType, null);
    }

    @Override
    public Mono<Void> createBucket(String bucket) {
        try {
            return Mono.fromFuture(
                    minioAsyncClient.makeBucket(this.makeBucketArgs(bucket))
            );
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    @Override
    public Flux<Item> list() {
        return listObjects("", null, false);
    }

    @Override
    public Flux<Item> list(String bucket) {
        return listObjects("", bucket, false);
    }

    @Override
    public Flux<Item> fullList() {
        return listObjects("", null, true);
    }

    @Override
    public Flux<Item> fullList(String bucket) {
        return listObjects("", bucket, true);
    }

    @Override
    public Flux<Item> list(Path path) {
        return listObjects(path.toString(), null, false);
    }

    @Override
    public Flux<Item> list(Path path, String bucket) {
        return listObjects(path.toString(), bucket, false);
    }

    @Override
    public Flux<Item> getFullList(Path path) {
        return listObjects(path.toString(), null, true);
    }

    @Override
    public Flux<Item> getFullList(Path path, String bucket) {
        return listObjects(path.toString(), bucket, true);
    }

    private Flux<Item> listObjects(String prefix, String bucket, boolean recursive) {

        if (bucket == null || bucket.isBlank()) {
            bucket = configurationProperties.getBucket();
        }

        String finalBucket = bucket;
        return Flux.defer(() -> {
            ListObjectsArgs args = ListObjectsArgs.builder()
                    .bucket(finalBucket)
                    .prefix(prefix)
                    .recursive(recursive)
                    .build();
            Iterable<Result<Item>> results = minioClient.listObjects(args);
            return Flux.fromIterable(results)
                    .publishOn(Schedulers.boundedElastic())
                    .map(result -> {
                        try {
                            return result.get();
                        } catch (Exception e) {
                            throw new MinioFetchException("Error while parsing list of objects", e);
                        }
                    });
        });
    }

    @Override
    public Mono<InputStream> get(Path path) {
        return this.get(path, configurationProperties.getBucket());
    }

    @Override
    public Mono<InputStream> get(Path path, String bucket) {
        return Mono.fromCallable(() -> {
                    GetObjectArgs args = GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(path.toString())
                            .build();
                    return minioClient.getObject(args);
                })
                .map(response -> (InputStream) response)  // Cast GetObjectResponse to InputStream
                .subscribeOn(Schedulers.boundedElastic())  // Run on a bounded elastic thread
                .onErrorMap(e -> new MinioFetchException("Error while fetching file in Minio", e));
    }

    @Override
    public Mono<StatObjectResponse> getMetadata(Path path) {
        return this.getMetadata(path, configurationProperties.getBucket());
    }

    @Override
    public Mono<StatObjectResponse> getMetadata(Path path, String bucket) {
        return Mono.fromCallable(() -> {
                    StatObjectArgs args = StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(path.toString())
                            .build();
                    return minioClient.statObject(args);
                }).subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(e -> new MinioFetchException("Error while fetching metadata in Minio", e));
    }

    @Override
    public Mono<Map<Path, StatObjectResponse>> getMetadata(Iterable<Path> paths) {
        return this.getMetadata(paths, configurationProperties.getBucket());
    }

    @Override
    public Mono<Map<Path, StatObjectResponse>> getMetadata(Iterable<Path> paths, String bucket) {
        return Flux.fromIterable(paths)
                .flatMap(path ->
                        Mono.fromCallable(() -> {
                                    StatObjectArgs args = StatObjectArgs.builder()
                                            .bucket(bucket)
                                            .object(path.toString())
                                            .build();
                                    return new AbstractMap.SimpleEntry<>(path, minioClient.statObject(args));
                                }).subscribeOn(Schedulers.boundedElastic())
                                .onErrorMap(e -> new MinioFetchException("Error while fetching metadata in Minio", e))
                )
                .collectMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue);
    }

    @Override
    public Mono<Void> getAndSave(Path source, String fileName) {
        return this.getAndSave(source, configurationProperties.getBucket(), fileName);
    }

    @Override
    public Mono<Void> getAndSave(Path source, String bucket, String fileName) {
        return Mono.fromRunnable(() -> {
            try {
                DownloadObjectArgs args = DownloadObjectArgs.builder()
                        .bucket(bucket)
                        .object(source.toString())
                        .filename(fileName)
                        .build();
                minioClient.downloadObject(args);
            } catch (Exception e) {
                throw new MinioFetchException("Error while downloading object from Minio", e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<Path> upload(Path source, InputStream file, Map<String, String> headers) {
        return this.upload(source, configurationProperties.getBucket(), file, headers);
    }

    @Override
    public Mono<Path> upload(Path source, String bucket, InputStream file, Map<String, String> headers) {

        return requireBucketOrCreate(bucket, true)
                .flatMap(unused ->
                        Mono.fromCallable(() -> {
                                    PutObjectArgs args = PutObjectArgs.builder()
                                            .bucket(bucket)
                                            .object(source.toString())
                                            .stream(file, file.available(), -1)
                                            .headers(headers)
                                            .build();
                                    minioClient.putObject(args);
                                    return source;
                                })
                                .subscribeOn(Schedulers.boundedElastic())
                );

    }

    private Mono<Path> putObject(PutObjectArgs putObjectArgs, Path source) {
        try {
            CompletableFuture<Path> future = minioAsyncClient.putObject(putObjectArgs)
                    .thenApply(response -> source)
                    .exceptionally(throwable -> null);
            return Mono.fromFuture(future);
        } catch (InsufficientDataException | InternalException | InvalidKeyException | IOException |
                 NoSuchAlgorithmException | XmlParserException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Mono<Path> upload(Path source, String bucket, FilePart filePart){

        Mono<Path> pathMono =  FileUtils.convert(filePart).flatMap(bytes -> {
            log.info("-----------------------upload");
            Map<String, String> headers = filePart.headers().toSingleValueMap();
            PutObjectArgs putObjectArgs = this.makePutObjectArgs(bucket, source.toString(), bytes, filePart.headers().getContentType(), headers);
            return this.putObject(putObjectArgs, source);
        });

        return this.requireBucketOrCreate(bucket, true).then(pathMono);
    }

    @Override
    public Mono<Void> upload(Path source, InputStream file) {
        return upload(source, file, "");
    }

    @Override
    public Mono<Void> upload(Path source, String bucket, InputStream file) {
        return upload(source, bucket, file, "");
    }

    @Override
    public Mono<Void> upload(Path source, InputStream file, String contentType, Map<String, String> headers) {
        return this.upload(source, configurationProperties.getBucket(), file, contentType, headers);
    }

    @Override
    public Mono<Void> upload(Path source, String bucket, InputStream file, String contentType, Map<String, String> headers) {
        return requireBucketOrCreate(bucket, true).flatMap(unused -> Mono.fromRunnable(() -> {
            try {
                PutObjectArgs args = PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(source.toString())
                        .stream(file, file.available(), -1)
                        .headers(headers)
                        .contentType(contentType)
                        .build();
                minioClient.putObject(args);
            } catch (Exception e) {
                throw new MinioFetchException("Error while uploading object to Minio", e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then());
    }

    @Override
    public Mono<Void> upload(Path source, InputStream file, String contentType) {
        return upload(source, file, contentType, Collections.emptyMap());
    }

    @Override
    public Mono<Void> upload(Path source, String bucket, InputStream file, String contentType) {
        return upload(source, bucket, file, contentType, Collections.emptyMap());
    }

    @Override
    public Mono<Void> upload(Path source, File file) {
        return this.upload(source, configurationProperties.getBucket(), file);
    }

    @Override
    public Mono<Void> upload(Path source, String bucket, File file) {
        return requireBucketOrCreate(bucket, true).flatMap(unused -> Mono.fromRunnable(() -> {
            try {
                UploadObjectArgs args = UploadObjectArgs.builder()
                        .bucket(bucket)
                        .object(source.toString())
                        .filename(file.getAbsolutePath())
                        .build();
                minioClient.uploadObject(args);
            } catch (Exception e) {
                throw new MinioFetchException("Error while uploading large file to Minio", e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then());
    }

    @Override
    public Mono<Void> remove(Path source) {
        return this.remove(source, configurationProperties.getBucket());
    }

    @Override
    public Mono<Void> remove(Path source, String bucket) {
        return Mono.fromRunnable(() -> {
            try {
                RemoveObjectArgs args = RemoveObjectArgs.builder()
                        .bucket(bucket)
                        .object(source.toString())
                        .build();
                minioClient.removeObject(args);
            } catch (Exception e) {
                throw new MinioFetchException("Error while removing file in Minio", e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<String> generatePresignedUrl(String service, String objectKey, int expiryInSeconds) {
        try {
            return Mono.just(minioAsyncClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(service)
                            .object(objectKey)
                            .expiry(expiryInSeconds)
                            .build()
            ));
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | XmlParserException |
                 ServerException e) {
            throw new RuntimeException(e);
        }
    }
}

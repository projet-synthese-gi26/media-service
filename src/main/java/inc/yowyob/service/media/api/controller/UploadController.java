package inc.yowyob.service.media.api.controller;

import inc.yowyob.service.media.api.dto.media.MediaDto;
import inc.yowyob.service.media.api.mappers.MediaMapper;
import inc.yowyob.service.media.application.services.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
@Slf4j
public class UploadController {

    private final MediaService mediaService;

    private final MediaMapper mediaMapper;

    /*@Value("${app.gateway-service}/${spring.application.name}/media/")
    private String mediaScheme;*/

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<MediaDto>> upload(
            @RequestPart("file") FilePart file,
            @RequestPart("service") String service,
            @RequestPart(value = "location") String location) {
        return mediaService.uploadMedia(file, service, location)
                .map(mediaMapper::toDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PostMapping(value = "/upload-multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Flux<MediaDto>>> uploadMultiple(
                                                                @RequestPart("files") Flux<FilePart> files,
                                                                @RequestPart("service") String service,
                                                                @RequestPart(value = "location") String location) {

        Flux<MediaDto> mediaDtoFlux = mediaService.uploadMultipleMedia(files, service, location)
                .map(mediaMapper::toDto);
        return Mono.just(ResponseEntity.ok(mediaDtoFlux));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<MediaDto>> replace(
            @PathVariable UUID id,
            @RequestPart("file") FilePart file,
            @RequestPart(value = "location", required = false) String location) {
        return mediaService.replaceMedia(id, file, location)
                .map(mediaMapper::toDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}

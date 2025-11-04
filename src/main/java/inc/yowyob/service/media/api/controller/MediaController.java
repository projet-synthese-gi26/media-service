package inc.yowyob.service.media.api.controller;

import inc.yowyob.service.media.api.dto.media.MediaDto;
import inc.yowyob.service.media.api.mappers.MediaMapper;
import inc.yowyob.service.media.application.services.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/media/infos")
@RequiredArgsConstructor
@Slf4j
public class MediaController {

    private final MediaService mediaService;

    private final MediaMapper mediaMapper;

    @GetMapping("/list")
    public Flux<MediaDto> list(@RequestParam String service) {
        return mediaService.listMedia(service).map(mediaMapper::toDto);
    }

    @GetMapping("/metadata/{id}")
    public Mono<ResponseEntity<MediaDto>> getMetadata(@PathVariable UUID id) {
        return mediaService.getMediaMetadata(id)
                .map(mediaMapper::toDto)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/search")
    public Flux<MediaDto> search(@RequestParam String name, @RequestParam String service) {
        return mediaService.searchMediaByName(service, name)
                .map(mediaMapper::toDto);
    }

}

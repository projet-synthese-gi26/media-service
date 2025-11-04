package inc.yowyob.service.media.api.controller;

import inc.yowyob.service.media.application.services.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
@Slf4j
public class DeleteController {

    private final MediaService mediaService;

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable UUID id) {
        return mediaService.deleteMedia(id)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @DeleteMapping("/path/{*path}")
    public Mono<ResponseEntity<Void>> deleteByPath(@PathVariable String path) {
        return mediaService.deleteMediaByPath(path)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

}

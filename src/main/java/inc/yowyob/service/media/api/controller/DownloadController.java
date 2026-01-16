package inc.yowyob.service.media.api.controller;

import inc.yowyob.service.media.application.services.MediaService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
@Slf4j
public class DownloadController {

    private final MediaService mediaService;

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Flux<DataBuffer>>> download(@PathVariable UUID id) {
        return mediaService.getMediaMetadata(id)
                .flatMap(media -> mediaService.downloadMedia(id)
                        .map(bytes -> {
                            Flux<DataBuffer> dataBufferFlux = Flux.just(new DefaultDataBufferFactory().wrap(bytes));
                            return ResponseEntity.ok()
                                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + media.getName() + "\"")
                                    .contentType(MediaType.parseMediaType(media.getMime()))
                                    .body(dataBufferFlux);
                        })
                )
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/proxy/{id}")
    public Mono<Void> proxyMedia(@PathVariable UUID id, @Parameter(hidden = true) ServerWebExchange exchange) {
        return mediaService.getMediaUrl(id, 3600).flatMap(url -> Mono.fromRunnable(() -> {
            exchange.getResponse().setStatusCode(HttpStatus.FOUND);
            exchange.getResponse().getHeaders().setLocation(URI.create(url));
        }));
    }

}

package inc.yowyob.service.media.infrastructure.persistence.repositories;

import inc.yowyob.service.media.infrastructure.persistence.entities.Media;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @author ETOUGUE
 */
@Repository
public interface MediaRepository extends R2dbcRepository<Media, UUID> {
    Flux<Media> findByService(String service);

    Mono<Media> findByServiceAndId(String service, UUID id);

    Mono<Media> findByPath(String path);

    Mono<Media> findByServiceAndPath(String service, String path);

    Flux<Media> findByServiceAndNameContaining(String service, String name);

    Flux<Media> findByServiceAndNameContainingIgnoreCase(String service, String name);

}


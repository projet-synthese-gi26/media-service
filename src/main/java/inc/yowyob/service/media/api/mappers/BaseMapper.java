package inc.yowyob.service.media.api.mappers;

import java.util.List;

/**
 * Interface de base pour les mappers.
 * @param <E> le type de l'entité (Entity)
 * @param <D> le type du DTO (Data Transfer Object)
 */
public interface BaseMapper<E, D> {

    D toDto(E entity);


}

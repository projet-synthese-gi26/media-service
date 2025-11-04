package inc.yowyob.service.media.api.mappers;

import inc.yowyob.service.media.api.dto.media.MediaDto;
import inc.yowyob.service.media.infrastructure.persistence.entities.Media;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * @author douglas
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MediaMapper extends BaseMapper<Media, MediaDto> {

}


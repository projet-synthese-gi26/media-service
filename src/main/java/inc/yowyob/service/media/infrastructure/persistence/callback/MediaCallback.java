package inc.yowyob.service.media.infrastructure.persistence.callback;

import inc.yowyob.service.media.infrastructure.persistence.entities.Media;
//import inc.yowyob.utils.UuidUtils;
//import inc.yowyob.utils.common.FieldInitUtil;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class MediaCallback implements BeforeConvertCallback<Media> {

    @Override
    public Publisher<Media> onBeforeConvert(Media media, SqlIdentifier table) {
        //FieldInitUtil.initIfNull(media::getId, media::setId, UuidUtils.timeBasedUUID());

        return Mono.just(media);
    }
}

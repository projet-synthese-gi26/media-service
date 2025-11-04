package inc.yowyob.service.media.infrastructure.converters;

import inc.yowyob.service.media.application.enums.MediaType;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToMediaTypeConverter implements Converter<String, MediaType> {

    @Override
    public MediaType convert(@NotNull String value) {
        return MediaType.fromValue(value);
    }
}

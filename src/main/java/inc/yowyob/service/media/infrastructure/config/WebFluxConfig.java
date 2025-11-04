package inc.yowyob.service.media.infrastructure.config;

import inc.yowyob.service.media.infrastructure.converters.StringToMediaTypeConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class WebFluxConfig implements WebFluxConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToMediaTypeConverter());
    }
}

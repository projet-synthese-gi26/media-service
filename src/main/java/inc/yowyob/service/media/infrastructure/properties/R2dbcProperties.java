package inc.yowyob.service.media.infrastructure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.r2dbc")
public class R2dbcProperties {
    private String url;
    private String username;
    private String password;
}
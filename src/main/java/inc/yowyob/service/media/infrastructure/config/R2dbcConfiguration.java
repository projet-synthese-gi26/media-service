package inc.yowyob.service.media.infrastructure.config;

import inc.yowyob.service.media.infrastructure.properties.R2dbcProperties;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@RequiredArgsConstructor
public class R2dbcConfiguration {

    private final R2dbcProperties r2dbcProperties;

    /**
     * Crée manuellement le bean ConnectionFactory à partir des propriétés
     * chargées dans R2dbcProperties.
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactoryOptions options = ConnectionFactoryOptions.parse(r2dbcProperties.getUrl())
                .mutate()
                .option(ConnectionFactoryOptions.USER, r2dbcProperties.getUsername())
                .option(ConnectionFactoryOptions.PASSWORD, r2dbcProperties.getPassword())
                .build();
        return ConnectionFactories.get(options);
    }

    /**
     * Crée le bean R2dbcEntityTemplate.
     * Spring va lui injecter la ConnectionFactory que nous créons juste au-dessus.
     */
    @Bean
    public R2dbcEntityTemplate r2dbcEntityTemplate(ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }
}
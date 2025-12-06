package inc.yowyob.service.media.infrastructure.config;



import inc.yowyob.service.media.infrastructure.properties.R2dbcProperties;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.PostgresDialect;
import org.springframework.data.r2dbc.mapping.R2dbcMappingContext;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class R2dbcConfiguration {

    private final R2dbcProperties r2dbcProperties;


    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactoryOptions options = ConnectionFactoryOptions.parse(r2dbcProperties.getUrl())
                .mutate()
                .option(ConnectionFactoryOptions.USER, r2dbcProperties.getUsername())
                .option(ConnectionFactoryOptions.PASSWORD, r2dbcProperties.getPassword())
                .build();
        return ConnectionFactories.get(options);
    }


    @Bean
    public R2dbcMappingContext r2dbcMappingContext() {
        return new R2dbcMappingContext();
    }


    @Bean
    public R2dbcEntityTemplate r2dbcEntityTemplate(ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }

    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions(ConnectionFactory connectionFactory) {
        PostgresDialect dialect = PostgresDialect.INSTANCE;
        List<Object> converters = new ArrayList<>(dialect.getConverters());
        converters.addAll(R2dbcCustomConversions.STORE_CONVERTERS);


        return new R2dbcCustomConversions(
                R2dbcCustomConversions.StoreConversions.of(dialect.getSimpleTypeHolder(), converters),
                List.of()
        );
    }
}

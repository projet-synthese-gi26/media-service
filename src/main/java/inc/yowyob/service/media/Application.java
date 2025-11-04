package inc.yowyob.service.media;

import inc.yowyob.service.media.infrastructure.properties.MinioConfigurationProperties;
import inc.yowyob.service.media.infrastructure.properties.R2dbcProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.reactive.config.EnableWebFlux;


/**
 * Hello world!
 *
 */
@EnableConfigurationProperties(value = {
        MinioConfigurationProperties.class,
        R2dbcProperties.class
})
@SpringBootApplication(exclude = {
        RedisAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        LiquibaseAutoConfiguration.class,
        R2dbcAutoConfiguration.class,
})
//@EnableAutoConfiguration(exclude = {LiquibaseAutoConfiguration.class})
@EnableDiscoveryClient
@ComponentScan(basePackages = {
        "inc.yowyob.*",
})
@RefreshScope
@EnableWebFlux
@EnableAsync
@EnableR2dbcRepositories
@EnableR2dbcAuditing
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

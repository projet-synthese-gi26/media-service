package inc.yowyob.service.media.application.models;


import inc.yowyob.service.media.infrastructure.properties.MinioConfigurationProperties;
import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementContextAutoConfiguration;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/**
 * Set the Minio health indicator on Actuator.
 *
 * @author Jordan LEFEBURE
 */
@ConditionalOnClass(ManagementContextAutoConfiguration.class)
@Component
public class MinioHealthIndicator implements HealthIndicator {

    private final MinioClient minioClient;
    private final MinioConfigurationProperties minioConfigurationProperties;

    @Autowired
    public MinioHealthIndicator(MinioClient minioClient, MinioConfigurationProperties minioConfigurationProperties) {
        this.minioClient = minioClient;
        this.minioConfigurationProperties = minioConfigurationProperties;
    }


    @Override
    public Health health() {
        if (minioClient == null) {
            return Health.down().build();
        }

        try {
            BucketExistsArgs args = BucketExistsArgs.builder()
                    .bucket(minioConfigurationProperties.getBucket())
                    .build();
            if (minioClient.bucketExists(args)) {
                return Health.up()
                        .withDetail("bucketName", minioConfigurationProperties.getBucket())
                        .build();
            } else {
                return Health.down()
                        .withDetail("bucketName", minioConfigurationProperties.getBucket())
                        .build();
            }
        } catch (Exception e) {
            return Health.down(e)
                    .withDetail("bucketName", minioConfigurationProperties.getBucket())
                    .build();
        }
    }
}

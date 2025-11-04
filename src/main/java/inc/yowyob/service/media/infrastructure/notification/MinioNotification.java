package inc.yowyob.service.media.infrastructure.notification;

import io.minio.messages.NotificationRecords;
import org.springframework.scheduling.annotation.Async;

import java.lang.annotation.*;


/**
 * Add a listener to the Minio bucket, which handle the events given in the {@code value} parameter.
 * The annotated method should have a parameter {@link NotificationRecords} and return {@code void}.
 */

@Async
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MinioNotification {

    /**
     * All events that the method handler should receive. Values defined
     * in <a href="https://docs.min.io/docs/minio-bucket-notification-guide.html">Minio documentation</a> are allowed.
     */
    String[] value();

    /**
     * Prefix of the items that should be handled
     */
    String prefix() default "";

    /**
     * Suffix of the items that should be handled
     */
    String suffix() default "";

}

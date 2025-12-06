package inc.yowyob.service.media.infrastructure.config;

import inc.yowyob.service.media.infrastructure.notification.MinioNotification;
import inc.yowyob.service.media.infrastructure.properties.MinioConfigurationProperties;
import io.minio.ListenBucketNotificationArgs;
import io.minio.MinioClient;
import io.minio.messages.NotificationRecords;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;


@EnableAsync
@RequiredArgsConstructor
public class MinioNotificationConfiguration implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinioNotificationConfiguration.class);

    private final MinioClient minioClient;
    private final MinioConfigurationProperties minioConfigurationProperties;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Delay scanning until the full context is refreshed (no circular deps)
        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            Object bean = applicationContext.getBean(beanName);
            Class<?> beanClass = AopUtils.getTargetClass(bean);

            for (Method method : beanClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(MinioNotification.class)) {
                    validateMethod(method);
                    MinioNotification annotation = method.getAnnotation(MinioNotification.class);
                    startListener(bean, method, annotation);
                }
            }
        }
    }

    private void validateMethod(Method method) {
        if (method.getParameterCount() != 1 || method.getParameterTypes()[0] != NotificationRecords.class) {
            throw new IllegalArgumentException("Method " + method.getName() + " must have a single NotificationRecords parameter");
        }
    }

    @Async
    public void startListener(Object bean, Method method, MinioNotification annotation) {
        while (true) {
            try {
                LOGGER.info("Registering MinIO handler: method={} events={}", method.getName(), Arrays.toString(annotation.value()));
                ListenBucketNotificationArgs args = ListenBucketNotificationArgs.builder()
                        .bucket(minioConfigurationProperties.getBucket())
                        .prefix(annotation.prefix())
                        .suffix(annotation.suffix())
                        .events(annotation.value())
                        .build();

                try (var iterator = minioClient.listenBucketNotification(args)) {
                    while (iterator.hasNext()) {
                        NotificationRecords records = iterator.next().get();
                        try {
                            LOGGER.debug("Invoking MinIO handler method: {}", method.getName());
                            method.invoke(bean, records);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            LOGGER.error("Failed to invoke MinIO handler method: {}", method.getName(), e);
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Listener failed for method {}, retrying in 5 seconds...", method.getName(), e);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}

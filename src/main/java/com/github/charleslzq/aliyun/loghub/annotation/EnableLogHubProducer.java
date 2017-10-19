package com.github.charleslzq.aliyun.loghub.annotation;

import com.github.charleslzq.aliyun.loghub.config.producer.LogHubProducerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LogHubProducerConfiguration.class)
public @interface EnableLogHubProducer {
}

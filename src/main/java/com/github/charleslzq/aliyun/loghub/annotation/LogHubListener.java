package com.github.charleslzq.aliyun.loghub.annotation;
import org.springframework.messaging.handler.annotation.MessageMapping;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@MessageMapping
@Documented
@Repeatable(LogHubListeners.class)
public @interface LogHubListener {
    String configName();
}

package com.github.charleslzq.aliyun.loghub.annotation;

import com.aliyun.openservices.log.common.Logs;
import com.github.charleslzq.aliyun.loghub.listener.filter.LogGroupFilter;
import org.springframework.messaging.handler.annotation.MessageMapping;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@MessageMapping
@Documented
@Repeatable(LogHubListeners.class)
public @interface LogHubListener {
    String configName();

    String[] topics() default {};

    Class<?> target() default Logs.Log.class;

    String[] groupFilterBeanNames() default {};

    String[] logFilterBeanNames() default {};
}

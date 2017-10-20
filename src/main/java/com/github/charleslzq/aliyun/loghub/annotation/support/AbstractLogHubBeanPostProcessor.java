package com.github.charleslzq.aliyun.loghub.annotation.support;

import com.github.charleslzq.aliyun.loghub.producer.LogHubProducerTemplate;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public abstract class AbstractLogHubBeanPostProcessor implements BeanPostProcessor {

    protected final LogHubProducerTemplate logHubProducerTemplate;

    protected final DefaultListableBeanFactory defaultListableBeanFactory;

    protected final Class<? extends Annotation> annotationClass;

    protected final Class<?> fieldClass;

    protected Set<String> createdBeanNames = new HashSet<>();

    protected AbstractLogHubBeanPostProcessor(
            LogHubProducerTemplate logHubProducerTemplate,
            DefaultListableBeanFactory defaultListableBeanFactory,
            Class<? extends Annotation> annotationClass,
            Class<?> fieldClass) {
        this.logHubProducerTemplate = logHubProducerTemplate;
        this.defaultListableBeanFactory = defaultListableBeanFactory;
        this.annotationClass = annotationClass;
        this.fieldClass = fieldClass;
    }

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        Class targetClass = AopUtils.isAopProxy(o) ? AopUtils.getTargetClass(o) : o.getClass();
        Stream.of(targetClass.getDeclaredFields())
                .filter(this::filterField)
                .forEach(this::process);
        return o;
    }

    protected boolean filterField(Field field) {
        return field.isAnnotationPresent(annotationClass) && field.getType().equals(fieldClass);
    };

    protected abstract void process(Field field);
}

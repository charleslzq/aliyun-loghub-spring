package com.github.charleslzq.aliyun.loghub.annotation.support;

import com.github.charleslzq.aliyun.loghub.producer.LogHubProducerTemplate;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractLogHubBeanPostProcessor<T extends Annotation> implements BeanPostProcessor {

    private final LogHubProducerTemplate logHubProducerTemplate;

    private final DefaultListableBeanFactory defaultListableBeanFactory;

    private final Class<T> annotationClass;

    private final Class<?> beanClass;

    private Set<String> createdBeanNames = new HashSet<>();

    protected AbstractLogHubBeanPostProcessor(
            LogHubProducerTemplate logHubProducerTemplate,
            DefaultListableBeanFactory defaultListableBeanFactory,
            Class<T> annotationClass,
            Class<?> beanClass) {
        this.logHubProducerTemplate = logHubProducerTemplate;
        this.defaultListableBeanFactory = defaultListableBeanFactory;
        this.annotationClass = annotationClass;
        this.beanClass = beanClass;
    }

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        Class targetClass = AopUtils.isAopProxy(o) ? AopUtils.getTargetClass(o) : o.getClass();
        Stream.of(targetClass.getDeclaredFields())
                .map(this::extractAnnotationFromField)
                .flatMap(List::stream)
                .forEach(this::processAnnotation);
        Stream.concat(
                Stream.of(targetClass.getDeclaredMethods()),
                Stream.of(targetClass.getDeclaredConstructors()))
                .map(this::extractAnnotationFromExecutables)
                .flatMap(List::stream)
                .forEach(this::processAnnotation);
        return o;
    }

    private List<T> extractAnnotationFromField(Field field) {
        if (field.isAnnotationPresent(annotationClass)) {
            return Collections.singletonList(field.getAnnotation(annotationClass));
        } else {
            return Collections.emptyList();
        }
    }

    private List<T> extractAnnotationFromExecutables(Executable executable) {
        if (!executable.isAnnotationPresent(Autowired.class)) {
            return Collections.emptyList();
        }
        if (executable.isAnnotationPresent(annotationClass) && executable.getParameterCount() == 1 && executable.getParameterTypes()[0].equals(beanClass)) {
            return Collections.singletonList(executable.getAnnotation(annotationClass));
        }
        return Stream.of(executable.getParameters())
                .filter(parameter -> parameter.isAnnotationPresent(annotationClass) && parameter.getType().equals(beanClass))
                .map(parameter -> parameter.getAnnotation(annotationClass))
                .collect(Collectors.toList());
    }

    private void processAnnotation(T annotation) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        builder.addConstructorArgValue(logHubProducerTemplate);
        Map<String, Object> annotationAttributes = AnnotationUtils.getAnnotationAttributes(annotation);
        String beanName = beanClass.getSimpleName() + "-" + annotationAttributes.toString();
        if (!createdBeanNames.contains(beanName)) {
            addAdditionalConstructArgs(builder, annotation);
            AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(annotationClass);
            annotationAttributes.forEach(qualifier::setAttribute);
            builder.getBeanDefinition().addQualifier(qualifier);
            defaultListableBeanFactory.registerBeanDefinition(beanName, builder.getBeanDefinition());
            createdBeanNames.add(beanName);
        }
    }

    protected abstract void addAdditionalConstructArgs(BeanDefinitionBuilder builder, T annotation);
}

package com.github.charleslzq.aliyun.loghub.annotation.support;

import com.github.charleslzq.aliyun.loghub.producer.LogHubProducerTemplate;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        Stream.of(targetClass.getDeclaredMethods())
                .map(this::extractAnnotationFromMethod)
                .flatMap(List::stream)
                .forEach(this::processAnnotation);
        Stream.of(targetClass.getDeclaredConstructors())
                .map(this::extractAnnotationFromConstructor)
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

    private List<T> extractAnnotationFromMethod(Method method) {
        if (!method.isAnnotationPresent(Autowired.class)) {
            return Collections.emptyList();
        }
        if (method.isAnnotationPresent(annotationClass) && method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(beanClass)) {
            return Collections.singletonList(method.getAnnotation(annotationClass));
        }
        return Stream.of(method.getParameters())
                .filter(parameter -> parameter.isAnnotationPresent(annotationClass) && parameter.getType().equals(beanClass))
                .map(parameter -> parameter.getAnnotation(annotationClass))
                .collect(Collectors.toList());
    }

    private List<T> extractAnnotationFromConstructor(Constructor constructor) {
        if (!constructor.isAnnotationPresent(Autowired.class)) {
            return Collections.emptyList();
        }
        if (constructor.isAnnotationPresent(annotationClass) && constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0].equals(beanClass)) {
            return Collections.singletonList(constructor.getDeclaredAnnotation(annotationClass));
        }
        return Stream.of(constructor.getParameters())
                .filter(parameter -> parameter.isAnnotationPresent(annotationClass) && parameter.getType().equals(beanClass))
                .map(parameter -> parameter.getAnnotation(annotationClass))
                .collect(Collectors.toList());
    }

    private void processAnnotation(T annotation) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        builder.addConstructorArgValue(logHubProducerTemplate);
        String beanName = generateBeanName(annotation);
        if (!createdBeanNames.contains(beanName)) {
            addAdditionalConstructArgs(builder, annotation);
            AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(annotationClass);
            Stream.of(annotationClass.getDeclaredMethods())
                    .collect(Collectors.toMap(
                            Method::getName,
                            method -> {
                                try {
                                    return method.invoke(annotation);
                                } catch (Exception e) {
                                    return null;
                                }
                            }
                    )).forEach(qualifier::setAttribute);
            builder.getBeanDefinition().addQualifier(qualifier);
            defaultListableBeanFactory.registerBeanDefinition(beanName, builder.getBeanDefinition());
            createdBeanNames.add(beanName);
        }
    }

    protected abstract String generateBeanName(T annotation);

    protected abstract void addAdditionalConstructArgs(BeanDefinitionBuilder builder, T annotation);
}

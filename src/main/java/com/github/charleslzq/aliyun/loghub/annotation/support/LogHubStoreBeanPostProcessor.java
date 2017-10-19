package com.github.charleslzq.aliyun.loghub.annotation.support;

import com.github.charleslzq.aliyun.loghub.annotation.LogHubStore;
import com.github.charleslzq.aliyun.loghub.producer.LogHubProducerTemplate;
import com.github.charleslzq.aliyun.loghub.producer.LogHubStoreTemplate;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.lang.reflect.Field;

public class LogHubStoreBeanPostProcessor extends AbstractLogHubBeanPostProcessor {
    public LogHubStoreBeanPostProcessor(
            LogHubProducerTemplate logHubProducerTemplate,
            DefaultListableBeanFactory defaultListableBeanFactory) {
        super(logHubProducerTemplate, defaultListableBeanFactory, LogHubStore.class, LogHubStoreTemplate.class);
    }

    @Override
    protected void process(Field field) {
        LogHubStore logHubStore = field.getAnnotation(LogHubStore.class);
        String project = logHubStore.project();
        String store = logHubStore.store();
        String beanName = "loghubProject-" + project + "-store-" + store;
        if (!createdBeanNames.contains(beanName)) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(LogHubStoreTemplate.class);
            builder.addConstructorArgValue(logHubProducerTemplate)
                    .addConstructorArgValue(project)
                    .addConstructorArgValue(store);
            AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(LogHubStore.class);
            builder.getBeanDefinition().addQualifier(qualifier);
            builder.getBeanDefinition().getQualifier(qualifier.getTypeName()).setAttribute("project", project);
            builder.getBeanDefinition().getQualifier(qualifier.getTypeName()).setAttribute("store", store);
            defaultListableBeanFactory.registerBeanDefinition(beanName, builder.getBeanDefinition());
            createdBeanNames.add(beanName);
        }
    }
}

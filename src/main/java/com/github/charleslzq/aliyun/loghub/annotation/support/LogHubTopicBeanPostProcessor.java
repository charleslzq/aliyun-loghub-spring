package com.github.charleslzq.aliyun.loghub.annotation.support;

import com.github.charleslzq.aliyun.loghub.annotation.LogHubTopic;
import com.github.charleslzq.aliyun.loghub.producer.LogHubProducerTemplate;
import com.github.charleslzq.aliyun.loghub.producer.LogHubTopicTemplate;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.lang.reflect.Field;

public class LogHubTopicBeanPostProcessor extends AbstractLogHubBeanPostProcessor {
    public LogHubTopicBeanPostProcessor(
            LogHubProducerTemplate logHubProducerTemplate,
            DefaultListableBeanFactory defaultListableBeanFactory) {
        super(logHubProducerTemplate, defaultListableBeanFactory, LogHubTopic.class, LogHubTopicTemplate.class);
    }

    @Override
    protected void process(Field field) {
        LogHubTopic logHubTopic = field.getAnnotation(LogHubTopic.class);
        String project = logHubTopic.project();
        String store = logHubTopic.store();
        String topic = logHubTopic.topic();
        String beanName = "loghubProject-" + project + "-store-" + store + "-topic-" + topic;
        if (!createdBeanNames.contains(beanName)) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(LogHubTopicTemplate.class);
            builder.addConstructorArgValue(logHubProducerTemplate)
                    .addConstructorArgValue(project)
                    .addConstructorArgValue(store)
                    .addConstructorArgValue(topic);
            AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(LogHubTopic.class);
            builder.getBeanDefinition().addQualifier(qualifier);
            builder.getBeanDefinition().getQualifier(qualifier.getTypeName()).setAttribute("project", project);
            builder.getBeanDefinition().getQualifier(qualifier.getTypeName()).setAttribute("store", store);
            builder.getBeanDefinition().getQualifier(qualifier.getTypeName()).setAttribute("topic", topic);
            defaultListableBeanFactory.registerBeanDefinition(beanName, builder.getBeanDefinition());
            createdBeanNames.add(beanName);
        }
    }
}

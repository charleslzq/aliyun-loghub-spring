package com.github.charleslzq.aliyun.loghub.annotation.support;

import com.github.charleslzq.aliyun.loghub.annotation.LogHubProject;
import com.github.charleslzq.aliyun.loghub.producer.LogHubProducerTemplate;
import com.github.charleslzq.aliyun.loghub.producer.LogHubProjectTemplate;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.lang.reflect.Field;

public class LogHubProjectBeanPostProcessor extends AbstractLogHubBeanPostProcessor {

    public LogHubProjectBeanPostProcessor(
            LogHubProducerTemplate logHubProducerTemplate,
            DefaultListableBeanFactory defaultListableBeanFactory) {
        super(logHubProducerTemplate, defaultListableBeanFactory, LogHubProject.class, LogHubProjectTemplate.class);
    }

    @Override
    protected void process(Field field) {
        LogHubProject logHubProject = field.getAnnotation(LogHubProject.class);
        String project = logHubProject.project();
        String beanName = "loghubProject-" + project;
        if (!createdBeanNames.contains(beanName)) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(LogHubProjectTemplate.class);
            builder.addConstructorArgValue(logHubProducerTemplate)
                    .addConstructorArgValue(project);

            AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(LogHubProject.class);
            builder.getBeanDefinition().addQualifier(qualifier);
            builder.getBeanDefinition().getQualifier(qualifier.getTypeName()).setAttribute("project", project);
            defaultListableBeanFactory.registerBeanDefinition(beanName, builder.getBeanDefinition());
            createdBeanNames.add(beanName);
        }
    }
}

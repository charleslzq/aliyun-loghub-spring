package com.github.charleslzq.aliyun.loghub.annotation.support;

import com.github.charleslzq.aliyun.loghub.annotation.LogHubTopic;
import com.github.charleslzq.aliyun.loghub.producer.LogHubProducerTemplate;
import com.github.charleslzq.aliyun.loghub.producer.LogHubTopicTemplate;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public class LogHubTopicBeanPostProcessor extends AbstractLogHubBeanPostProcessor<LogHubTopic> {
    public LogHubTopicBeanPostProcessor(
            LogHubProducerTemplate logHubProducerTemplate,
            DefaultListableBeanFactory defaultListableBeanFactory) {
        super(logHubProducerTemplate, defaultListableBeanFactory, LogHubTopic.class, LogHubTopicTemplate.class);
    }

    @Override
    protected String generateBeanName(LogHubTopic annotation) {
        return "loghubProject-" + annotation.project() + "-store-" + annotation.store() + "-topic-" + annotation.topic();
    }

    @Override
    protected void addAdditionalConstructArgs(BeanDefinitionBuilder builder, LogHubTopic annotation) {
        builder.addConstructorArgValue(annotation.project())
                .addConstructorArgValue(annotation.store())
                .addConstructorArgValue(annotation.topic());
    }
}

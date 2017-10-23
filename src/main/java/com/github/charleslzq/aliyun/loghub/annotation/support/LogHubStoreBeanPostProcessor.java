package com.github.charleslzq.aliyun.loghub.annotation.support;

import com.github.charleslzq.aliyun.loghub.annotation.LogHubStore;
import com.github.charleslzq.aliyun.loghub.producer.LogHubProducerTemplate;
import com.github.charleslzq.aliyun.loghub.producer.LogHubStoreTemplate;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;


public class LogHubStoreBeanPostProcessor extends AbstractLogHubBeanPostProcessor<LogHubStore> {
    public LogHubStoreBeanPostProcessor(
            LogHubProducerTemplate logHubProducerTemplate,
            DefaultListableBeanFactory defaultListableBeanFactory) {
        super(logHubProducerTemplate, defaultListableBeanFactory, LogHubStore.class, LogHubStoreTemplate.class);
    }

    @Override
    protected void addConstructorArgValues(BeanDefinitionBuilder builder, LogHubProducerTemplate logHubProducerTemplate, LogHubStore annotation) {
        builder.addConstructorArgValue(logHubProducerTemplate);
        builder.addConstructorArgValue(annotation.project());
        builder.addConstructorArgValue(annotation.store());
    }
}

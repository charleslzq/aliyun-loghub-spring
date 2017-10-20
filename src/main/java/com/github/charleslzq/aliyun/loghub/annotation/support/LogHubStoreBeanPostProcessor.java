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
    protected void addAdditionalConstructArgs(BeanDefinitionBuilder builder, LogHubStore annotation) {
        builder.addConstructorArgValue(annotation.project())
                .addConstructorArgValue(annotation.store());
    }
}

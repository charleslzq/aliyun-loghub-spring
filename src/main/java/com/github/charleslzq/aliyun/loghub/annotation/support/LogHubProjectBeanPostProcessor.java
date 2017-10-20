package com.github.charleslzq.aliyun.loghub.annotation.support;

import com.github.charleslzq.aliyun.loghub.annotation.LogHubProject;
import com.github.charleslzq.aliyun.loghub.producer.LogHubProducerTemplate;
import com.github.charleslzq.aliyun.loghub.producer.LogHubProjectTemplate;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public class LogHubProjectBeanPostProcessor extends AbstractLogHubBeanPostProcessor<LogHubProject> {

    public LogHubProjectBeanPostProcessor(
            LogHubProducerTemplate logHubProducerTemplate,
            DefaultListableBeanFactory defaultListableBeanFactory) {
        super(logHubProducerTemplate, defaultListableBeanFactory, LogHubProject.class, LogHubProjectTemplate.class);
    }
}

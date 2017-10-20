package com.github.charleslzq.aliyun.loghub.config.consumer;

import com.github.charleslzq.aliyun.loghub.annotation.support.LogHubListenerBeanPostProcessor;
import com.github.charleslzq.aliyun.loghub.config.LogHubProjectProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Slf4j
@Configuration
@EnableConfigurationProperties({
        LogHubProjectProperties.class,
        LogHubConsumerProperties.class
})
public class LogHubConsumerConfiguration {

    @Autowired
    private LogHubProjectProperties logHubProjectProperties;

    @Autowired
    private LogHubConsumerProperties logHubConsumerProperties;

    @Bean
    @ConditionalOnMissingBean(name = "clientWorkerExecutor")
    public AsyncTaskExecutor clientWorkExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    public LogHubListenerBeanPostProcessor logHubListenerBeanPostProcessor(
            @Qualifier("clientWorkerExecutor") AsyncTaskExecutor taskExecutor
    ) {
        return new LogHubListenerBeanPostProcessor(
                taskExecutor,
                logHubProjectProperties,
                logHubConsumerProperties
        );
    }

}

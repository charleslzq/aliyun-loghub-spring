package com.github.charleslzq.aliyun.loghub.config.consumer;

import com.github.charleslzq.aliyun.loghub.annotation.support.LogHubListenerBeanPostProcessor;
import com.github.charleslzq.aliyun.loghub.config.LogHubProjectProperties;
import com.github.charleslzq.aliyun.loghub.listener.LogHubMessageHandlerMethodFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

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
    public AsyncTaskExecutor clientWorkerExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    @ConditionalOnMissingBean(name = "logHubMessageHandlerMethodFactory")
    public MessageHandlerMethodFactory logHubMessageHandlerMethodFactory() {
        return new LogHubMessageHandlerMethodFactory();
    }

    @Bean
    @ConditionalOnMissingBean(name = "logHubConsumerConversionService")
    public ConversionService logHubConsumerConversionService() {
        return new DefaultConversionService();
    }

    @Bean
    public LogHubListenerBeanPostProcessor logHubListenerBeanPostProcessor(
            @Qualifier("clientWorkerExecutor") AsyncTaskExecutor taskExecutor,
            @Qualifier("logHubMessageHandlerMethodFactory") MessageHandlerMethodFactory messageHandlerMethodFactory,
            @Qualifier("logHubConsumerConversionService") ConversionService conversionService
    ) {
        return new LogHubListenerBeanPostProcessor(
                taskExecutor,
                logHubProjectProperties,
                logHubConsumerProperties,
                messageHandlerMethodFactory,
                conversionService
        );
    }
}

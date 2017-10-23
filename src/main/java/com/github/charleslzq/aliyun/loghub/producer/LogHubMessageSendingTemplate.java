package com.github.charleslzq.aliyun.loghub.producer;

import org.springframework.messaging.Message;
import org.springframework.messaging.core.AbstractMessageSendingTemplate;

import java.util.Collections;

public class LogHubMessageSendingTemplate extends AbstractMessageSendingTemplate<LogHubDestination> {
    private final LogHubProducerTemplate logHubProducerTemplate;

    public LogHubMessageSendingTemplate(LogHubProducerTemplate logHubProducerTemplate) {
        this.logHubProducerTemplate = logHubProducerTemplate;
    }

    @Override
    protected void doSend(LogHubDestination logHubDestination, Message<?> message) {
        this.logHubProducerTemplate.send(
                logHubDestination.getProject(),
                logHubDestination.getStore(),
                logHubDestination.getTopic(),
                Collections.singletonList(message)
        );
    }
}

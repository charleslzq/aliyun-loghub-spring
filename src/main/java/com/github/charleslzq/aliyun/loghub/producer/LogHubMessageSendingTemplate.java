package com.github.charleslzq.aliyun.loghub.producer;

import com.github.charleslzq.aliyun.loghub.producer.destination.DestinationResolver;
import com.github.charleslzq.aliyun.loghub.producer.destination.LogHubDestination;
import org.springframework.messaging.Message;
import org.springframework.messaging.core.AbstractMessageSendingTemplate;

import java.util.Collections;

public class LogHubMessageSendingTemplate extends AbstractMessageSendingTemplate<String> {
    private final LogHubProducerTemplate logHubProducerTemplate;
    private final DestinationResolver<LogHubDestination> destinationResolver;

    public LogHubMessageSendingTemplate(LogHubProducerTemplate logHubProducerTemplate, DestinationResolver<LogHubDestination> destinationResolver) {
        this.logHubProducerTemplate = logHubProducerTemplate;
        this.destinationResolver = destinationResolver;
    }

    @Override
    protected void doSend(String destination, Message<?> message) {
        LogHubDestination logHubDestination = destinationResolver.resolveDestination(destination);
        this.logHubProducerTemplate.send(
                logHubDestination.getProject(),
                logHubDestination.getStore(),
                logHubDestination.getTopic(),
                Collections.singletonList(message.getPayload())
        );
    }
}

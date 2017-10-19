package com.github.charleslzq.aliyun.loghub.producer;

import com.aliyun.openservices.log.producer.ILogCallback;

import java.util.List;

public class LogHubProjectTemplate {
    private final LogHubProducerTemplate logHubProducerTemplate;
    private final String project;

    public LogHubProjectTemplate(LogHubProducerTemplate logHubProducerTemplate, String project) {
        this.logHubProducerTemplate = logHubProducerTemplate;
        this.project = project;
    }

    public void send(String store, String topic, List<?> items, ILogCallback callback) {
        logHubProducerTemplate.send(project, store, topic, items, callback);
    }

    public void send(String store, String topic, List<?> items) {
        logHubProducerTemplate.send(project, store, topic, items);
    }
}

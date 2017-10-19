package com.github.charleslzq.aliyun.loghub.producer;

import com.aliyun.openservices.log.producer.ILogCallback;

import java.util.List;

public class LogHubTopicTemplate {
    private final LogHubProducerTemplate logHubProducerTemplate;
    private final String project;
    private final String store;
    private final String topic;

    public LogHubTopicTemplate(LogHubProducerTemplate logHubProducerTemplate, String project, String store, String topic) {
        this.logHubProducerTemplate = logHubProducerTemplate;
        this.project = project;
        this.store = store;
        this.topic = topic;
    }

    public void send(List<?> items, ILogCallback callback) {
        logHubProducerTemplate.send(project, store, topic, items, callback);
    }

    public void send(List<?> items) {
        logHubProducerTemplate.send(project, store, topic, items);
    }
}

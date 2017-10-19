package com.github.charleslzq.aliyun.loghub.producer;

import com.aliyun.openservices.log.producer.ILogCallback;

import java.util.List;

public class LogHubStoreTemplate {
    private final LogHubProducerTemplate logHubProducerTemplate;
    private final String project;
    private final String store;

    public LogHubStoreTemplate(LogHubProducerTemplate logHubProducerTemplate, String project, String store) {
        this.logHubProducerTemplate = logHubProducerTemplate;
        this.project = project;
        this.store = store;
    }

    public void send(String topic, List<?> items, ILogCallback callback) {
        logHubProducerTemplate.send(project, store, topic, items, callback);
    }

    public void send(String topic, List<?> items) {
        logHubProducerTemplate.send(project, store, topic, items);
    }
}

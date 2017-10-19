package com.github.charleslzq.aliyun.loghub.producer;

import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.producer.ILogCallback;
import com.aliyun.openservices.log.producer.LogProducer;
import org.springframework.core.convert.ConversionService;

import java.util.List;
import java.util.stream.Collectors;

public class LogHubProducerTemplate {
    /**
     * aliyun log producer, send message to loghub
     */
    private final LogProducer logProducer;
    /**
     * indicate the machine, maybe ip or host name
     */
    private final String source;
    /**
     * conversionService, convert any object to LogItem
     */
    private final ConversionService conversionService;
    /**
     * available loghub projects
     */
    private final List<String> availableProjects;


    public LogHubProducerTemplate(LogProducer logProducer, String source, ConversionService conversionService, List<String> availableProjects) {
        this.logProducer = logProducer;
        this.source = source;
        this.conversionService = conversionService;
        this.availableProjects = availableProjects;
    }

    public void send(String project, String store, String topic, List<?> items, ILogCallback callback) {
        if (availableProjects.contains(project)) {
            logProducer.send(project, store, topic, source,
                    items.stream().map(item -> conversionService.convert(item, LogItem.class)).collect(Collectors.toList()),
                    callback);
        } else {
            throw new IllegalArgumentException("Project " + project + " not configured");
        }
    }
}

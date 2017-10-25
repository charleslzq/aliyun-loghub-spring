package com.github.charleslzq.aliyun.loghub.producer;

import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.producer.ILogCallback;
import com.aliyun.openservices.log.producer.LogProducer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.convert.ConversionService;

import java.util.List;
import java.util.stream.Collectors;

public class LogHubProducerTemplate implements DisposableBean {
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
     * if true, send logs immediately when invoke send method
     */
    private final boolean flushImmediately;


    public LogHubProducerTemplate(LogProducer logProducer, String source, ConversionService conversionService, boolean flushImmediately) {
        this.logProducer = logProducer;
        this.source = source;
        this.conversionService = conversionService;
        this.flushImmediately = flushImmediately;
    }

    public void send(String project, String store, String topic, List<?> items, ILogCallback callback) {
        logProducer.send(project, store, topic, source,
                items.stream().map(item -> conversionService.convert(item, LogItem.class)).collect(Collectors.toList()),
                callback);
        if (flushImmediately) {
            logProducer.flush();
        }
    }

    public void send(String project, String store, String topic, List<?> items) {
        logProducer.send(project, store, topic, source,
                items.stream().map(item -> conversionService.convert(item, LogItem.class)).collect(Collectors.toList()));
        if (flushImmediately) {
            logProducer.flush();
        }
    }

    @Override
    public void destroy() throws Exception {
        logProducer.close();
    }
}

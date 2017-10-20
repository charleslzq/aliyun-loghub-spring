package com.github.charleslzq.aliyun.loghub.listener;

import com.aliyun.openservices.log.common.LogGroupData;
import com.aliyun.openservices.loghub.client.interfaces.ILogHubProcessor;
import com.aliyun.openservices.loghub.client.interfaces.ILogHubProcessorFactory;

import java.util.List;
import java.util.function.Consumer;

public class ListenerProcessorFactory implements ILogHubProcessorFactory {
    private final Consumer<List<LogGroupData>> logConsumer;

    public ListenerProcessorFactory(Consumer<List<LogGroupData>> logConsumer) {
        this.logConsumer = logConsumer;
    }

    @Override
    public ILogHubProcessor generatorProcessor() {
        return new ListenerProcessor(logConsumer);
    }
}

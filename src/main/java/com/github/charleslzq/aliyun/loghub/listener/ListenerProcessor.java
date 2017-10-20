package com.github.charleslzq.aliyun.loghub.listener;

import com.aliyun.openservices.log.common.LogGroupData;
import com.aliyun.openservices.loghub.client.ILogHubCheckPointTracker;
import com.aliyun.openservices.loghub.client.exceptions.LogHubCheckPointException;
import com.aliyun.openservices.loghub.client.interfaces.ILogHubProcessor;

import java.util.List;
import java.util.function.Consumer;

class ListenerProcessor implements ILogHubProcessor {
    private final Consumer<List<LogGroupData>> logConsumer;
    private int shardId;
    private long lastCheckTime = 0;

    public ListenerProcessor(Consumer<List<LogGroupData>> logConsumer) {
        this.logConsumer = logConsumer;
    }

    @Override
    public void initialize(int shardId) {
        this.shardId = shardId;
    }

    @Override
    public String process(List<LogGroupData> logs, ILogHubCheckPointTracker iLogHubCheckPointTracker) {
        try {
            logConsumer.accept(logs);
        } catch (Exception e) {
            e.printStackTrace();
            return iLogHubCheckPointTracker.getCheckPoint();
        }

        long curTime = System.currentTimeMillis();
        if (curTime - lastCheckTime > 60 * 1000) {
            try {
                iLogHubCheckPointTracker.saveCheckPoint(true);
            } catch (LogHubCheckPointException e) {
                e.printStackTrace();
            }
            lastCheckTime = curTime;
        } else {
            try {
                iLogHubCheckPointTracker.saveCheckPoint(false);
            } catch (LogHubCheckPointException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public void shutdown(ILogHubCheckPointTracker iLogHubCheckPointTracker) {
        try {
            iLogHubCheckPointTracker.saveCheckPoint(true);
        } catch (LogHubCheckPointException e) {
            e.printStackTrace();
        }
    }
}

package com.github.charleslzq.aliyun.loghub.config.consumer;

import com.aliyun.openservices.loghub.client.config.LogHubConfig;
import com.aliyun.openservices.loghub.client.config.LogHubCursorPosition;
import lombok.Data;

@Data
public class LogHubConsumerConfig {
    /**
     * the project to consume message
     */
    private String project;
    /**
     * the store to consume message
     */
    private String store;
    /**
     * the group name this consumer will use.
     */
    private String groupName;
    /**
     * the name of this consumer
     */
    private String consumerName;
    /**
     * from which time to read messages.
     */
    private LogHubCursorPosition cursorPosition = LogHubCursorPosition.END_CURSOR;
    /**
     * the offset to read messages. take effect only when cursorPosition is specified as SPECIAL_TIMER_CURSOR.
     */
    private int startTime = 0;
    /**
     * interval of attempts to fetch data from server
     */
    private long fetchIntervalMillis = 200;
    /**
     * interval of heartBeat check
     */
    private long heartBeatIntervalMillis = 10000;
    /**
     * whether to keep message in order or not
     */
    private boolean keepOrder = true;

    private LogHubConfig generateLogHubConfig(
            String endpoint,
            String accessId,
            String accessKey,
            String groupName,
            LogHubCursorPosition cursorPosition,
            int startTime,
            long fetchIntervalMillis,
            long heartBeatIntervalMillis,
            boolean keepOrder
    ) {
        if (LogHubCursorPosition.SPECIAL_TIMER_CURSOR == cursorPosition) {
            LogHubConfig config = new LogHubConfig(
                    groupName,
                    consumerName,
                    endpoint,
                    project,
                    store,
                    accessId,
                    accessKey,
                    startTime,
                    heartBeatIntervalMillis,
                    keepOrder
            );
            config.setDataFetchIntervalMillis(fetchIntervalMillis);
            return config;
        } else {
            LogHubConfig config = new LogHubConfig(
                    groupName,
                    consumerName,
                    endpoint,
                    project,
                    store,
                    accessId,
                    accessKey,
                    cursorPosition,
                    heartBeatIntervalMillis,
                    keepOrder
            );
            config.setDataFetchIntervalMillis(fetchIntervalMillis);
            return config;
        }
    }

    public LogHubConfig generateLogHubConfig(
            String endpoint,
            String accessId,
            String accessKey
    ) {
        return generateLogHubConfig(
                endpoint,
                accessId,
                accessKey,
                groupName,
                cursorPosition,
                startTime,
                fetchIntervalMillis,
                heartBeatIntervalMillis,
                keepOrder
        );
    }
}

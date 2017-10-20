package com.github.charleslzq.aliyun.loghub.listener;

import com.aliyun.openservices.log.common.LogGroupData;
import com.aliyun.openservices.loghub.client.ClientWorker;
import com.aliyun.openservices.loghub.client.config.LogHubConfig;
import com.aliyun.openservices.loghub.client.exceptions.LogHubClientWorkerException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.task.AsyncTaskExecutor;

import java.util.List;
import java.util.function.Consumer;

public class ClientWorkerContainer implements SmartLifecycle {
    private final AsyncTaskExecutor taskExecutor;
    private final LogHubConfig logHubConfig;
    private final Consumer<List<LogGroupData>> logConsumer;

    @Getter
    @Setter
    private boolean running = false;
    @Getter
    @Setter
    private boolean autoStartup = true;
    @Getter
    @Setter
    private int phase = 0;
    private Object lifeCycleMonitor = new Object();

    public ClientWorkerContainer(AsyncTaskExecutor taskExecutor, LogHubConfig logHubConfig, Consumer<List<LogGroupData>> logConsumer) {
        this.taskExecutor = taskExecutor;
        this.logHubConfig = logHubConfig;
        this.logConsumer = logConsumer;
    }

    protected void doStart() {
        ListenerProcessorFactory listenerProcessorFactory = new ListenerProcessorFactory(logConsumer);
        try {
            ClientWorker clientWorker = new ClientWorker(listenerProcessorFactory, logHubConfig);
            taskExecutor.submit(clientWorker);
        } catch (LogHubClientWorkerException e) {
            e.printStackTrace();
        }
    }

    protected void doStop(Runnable runnable) {
        runnable.run();
    }

    @Override
    public void start() {
        synchronized (lifeCycleMonitor) {
            if (!this.isRunning()) {
                this.doStart();
                this.setRunning(true);
            }
        }
    }

    @Override
    public void stop() {
        synchronized (lifeCycleMonitor) {
            if (this.isRunning()) {
                this.doStop(() -> {
                });
                this.setRunning(false);
            }
        }
    }

    @Override
    public void stop(Runnable runnable) {
        synchronized (lifeCycleMonitor) {
            if (this.isRunning()) {
                this.doStop(runnable);
                this.setRunning(false);
            }
        }
    }
}

package com.github.charleslzq.aliyun.loghub.config.consumer;

import com.aliyun.openservices.loghub.client.config.LogHubConfig;
import com.github.charleslzq.aliyun.loghub.config.LogHubAccountConfig;
import com.github.charleslzq.aliyun.loghub.config.LogHubProjectConfig;
import com.github.charleslzq.aliyun.loghub.config.LogHubProjectProperties;
import com.github.charleslzq.aliyun.loghub.listener.ClientWorkerContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Slf4j
@Configuration
@EnableConfigurationProperties({
        LogHubProjectProperties.class,
        LogHubConsumerProperties.class
})
public class LogHubConsumerConfiguration {

    @Autowired
    private LogHubProjectProperties logHubProjectProperties;

    @Autowired
    private LogHubConsumerProperties logHubConsumerProperties;

    @PostConstruct
    public void init() {
        logHubConsumerProperties.getConsumers().forEach((configName, consumerConfig) -> {
            Optional<LogHubAccountConfig> accountConfigOptional = logHubProjectProperties.getAccounts().stream()
                    .filter(logHubAccountConfig -> logHubAccountConfig.getProjects().stream().anyMatch(
                            logHubProjectConfig -> logHubProjectConfig.getProject().equals(consumerConfig.getProject())
                    )).findAny();
            if (accountConfigOptional.isPresent()) {
                LogHubAccountConfig logHubAccountConfig = accountConfigOptional.get();
                Optional<LogHubProjectConfig> projectConfigOptional = logHubAccountConfig.getProjects().stream()
                        .filter(logHubProjectConfig -> logHubProjectConfig.getProject().equals(consumerConfig.getProject()))
                        .findAny();
                if (projectConfigOptional.isPresent()) {
                    LogHubProjectConfig logHubProjectConfig = projectConfigOptional.get();
                    LogHubConfig logHubConfig = consumerConfig.generateLogHubConfig(
                            logHubProjectConfig.getEndpoint(),
                            logHubAccountConfig.getAccessId(),
                            logHubAccountConfig.getAccessKey()
                    );
                    ClientWorkerContainer clientWorkerContainer = new ClientWorkerContainer(
                            new SimpleAsyncTaskExecutor(),
                            logHubConfig,
                            logGroupData -> {
                                log.info("receive data size {}", logGroupData.size());
                            }
                    );
                    clientWorkerContainer.start();
                }
            }

            log.warn("Project {} not configured", consumerConfig.getProject());

        });
    }

}

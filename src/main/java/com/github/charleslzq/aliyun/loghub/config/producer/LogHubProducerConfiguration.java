package com.github.charleslzq.aliyun.loghub.config.producer;

import com.aliyun.openservices.log.producer.LogProducer;
import com.github.charleslzq.aliyun.loghub.config.LogHubProjectConfig;
import com.github.charleslzq.aliyun.loghub.config.LogHubProjectProperties;
import com.github.charleslzq.aliyun.loghub.producer.DefaultLogItemConversionService;
import com.github.charleslzq.aliyun.loghub.producer.LogHubProducerTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableConfigurationProperties({
        LogHubProducerProperties.class,
        LogHubProjectProperties.class
})
public class LogHubProducerConfiguration {

    @Autowired
    private LogHubProducerProperties logHubProducerProperties;

    @Autowired
    private LogHubProjectProperties logHubProjectProperties;

    private String hostIp = "127.0.0.1";
    private String hostName = "localhost";

    @Bean
    @ConditionalOnMissingBean
    public ConversionService logHubProducerConversionService() {
        return new DefaultLogItemConversionService();
    }

    @Bean
    public LogHubProducerTemplate logHubProducerTemplate(ConversionService conversionService) {
        try {
            hostIp = InetAddress.getLocalHost().getHostAddress();
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.error("Error when accessing host ip and name", e);
        }
        String source = logHubProducerProperties.getSource() == SourceType.HOST_IP ? hostIp : hostName;
        List<String> availableProjects = logHubProjectProperties.getProjects().stream()
                .map(LogHubProjectConfig::getProject)
                .collect(Collectors.toList());
        LogProducer logProducer = new LogProducer(logHubProducerProperties.generateProducerConfig());
        logHubProjectProperties.getProjects().forEach(
                logHubProjectConfig -> logProducer.setProjectConfig(logHubProjectConfig.generateProjectConfig())
        );

        return new LogHubProducerTemplate(logProducer, source, conversionService, availableProjects);
    }
}

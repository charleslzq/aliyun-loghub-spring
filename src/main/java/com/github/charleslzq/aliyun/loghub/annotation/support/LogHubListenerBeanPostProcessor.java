package com.github.charleslzq.aliyun.loghub.annotation.support;

import com.aliyun.openservices.log.common.LogGroupData;
import com.aliyun.openservices.loghub.client.config.LogHubConfig;
import com.github.charleslzq.aliyun.loghub.annotation.LogHubListener;
import com.github.charleslzq.aliyun.loghub.annotation.LogHubListeners;
import com.github.charleslzq.aliyun.loghub.config.LogHubAccountConfig;
import com.github.charleslzq.aliyun.loghub.config.LogHubProjectConfig;
import com.github.charleslzq.aliyun.loghub.config.LogHubProjectProperties;
import com.github.charleslzq.aliyun.loghub.config.consumer.LogHubConsumerProperties;
import com.github.charleslzq.aliyun.loghub.listener.ClientWorkerContainer;
import com.github.charleslzq.aliyun.loghub.listener.MethodLogHubListenerEndpoint;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Slf4j
public class LogHubListenerBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware, InitializingBean, ApplicationListener<ApplicationReadyEvent> {
    private final AsyncTaskExecutor taskExecutor;
    private final LogHubProjectProperties logHubProjectProperties;
    private final LogHubConsumerProperties logHubConsumerProperties;
    private final Map<String, ClientWorkerContainer> containerMap = new ConcurrentHashMap<>();
    private final Multimap<String, Consumer<List<LogGroupData>>> listeners = ArrayListMultimap.create();
    private final MessageHandlerMethodFactory messageHandlerMethodFactory;
    private final ConversionService conversionService;
    @Setter
    private BeanFactory beanFactory;

    public LogHubListenerBeanPostProcessor(
            AsyncTaskExecutor taskExecutor,
            LogHubProjectProperties logHubProjectProperties,
            LogHubConsumerProperties logHubConsumerProperties,
            MessageHandlerMethodFactory messageHandlerMethodFactory, ConversionService conversionService) {
        this.taskExecutor = taskExecutor;
        this.logHubProjectProperties = logHubProjectProperties;
        this.logHubConsumerProperties = logHubConsumerProperties;
        this.messageHandlerMethodFactory = messageHandlerMethodFactory;
        this.conversionService = conversionService;
    }

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        Class targetClass = AopUtils.isAopProxy(o) ? AopUtils.getTargetClass(o) : o.getClass();
        Stream.of(targetClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(LogHubListener.class) || method.isAnnotationPresent(LogHubListeners.class))
                .forEach(method ->
                        Stream.of(method.getAnnotationsByType(LogHubListener.class))
                                .forEach(logHubListener -> process(logHubListener, method, o))
                );
        return o;
    }

    private void process(LogHubListener logHubListener, Method method, Object bean) {
        MethodLogHubListenerEndpoint endpoint = new MethodLogHubListenerEndpoint(
                bean,
                method,
                logHubListener,
                messageHandlerMethodFactory,
                conversionService);
        listeners.put(logHubListener.configName(), endpoint.getLogGroupListener(beanFactory));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logHubConsumerProperties.getConsumers().forEach((configName, consumerConfig) -> {
            if (!containerMap.containsKey(configName)) {
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
                                taskExecutor,
                                logHubConfig,
                                logGroupData -> listeners.get(configName).forEach(consumer -> consumer.accept(logGroupData))
                        );
                        containerMap.put(configName, clientWorkerContainer);
                    }
                }

                log.warn("Project {} not configured", consumerConfig.getProject());
            }
        });
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        containerMap.values().forEach(ClientWorkerContainer::start);
    }
}

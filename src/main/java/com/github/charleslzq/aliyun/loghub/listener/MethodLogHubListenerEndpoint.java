package com.github.charleslzq.aliyun.loghub.listener;

import com.aliyun.openservices.log.common.LogGroupData;
import com.aliyun.openservices.log.common.Logs;
import com.github.charleslzq.aliyun.loghub.annotation.LogHubListener;
import com.github.charleslzq.aliyun.loghub.listener.filter.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
import org.springframework.messaging.support.MessageBuilder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class MethodLogHubListenerEndpoint {
    private final Object bean;
    private final Method method;
    private final LogHubListener annotation;
    private final InvocableHandlerMethod invocableHandlerMethod;
    private final ConversionService conversionService;

    public MethodLogHubListenerEndpoint(
            Object bean,
            Method method,
            LogHubListener annotation,
            MessageHandlerMethodFactory messageHandlerMethodFactory,
            ConversionService conversionService) {
        this.bean = bean;
        this.method = method;
        this.annotation = annotation;
        this.conversionService = conversionService;
        this.invocableHandlerMethod = messageHandlerMethodFactory.createInvocableHandlerMethod(bean, method);
    }

    private void invokeMethod(Message<?> message) {
        try {
            this.invocableHandlerMethod.invoke(message);
        } catch (Exception e) {
            log.error("Error when invoking method " + method.getName()
                    + " in object " + bean.getClass().getSimpleName()
                    + "with message " + message.toString(), e);
        }
    }

    private LogGroupFilter composeLogGroupFilter(BeanFactory beanFactory) {
        String[] beanNames = annotation.groupFilterBeanNames();
        String[] topics = annotation.topics();
        List<LogGroupFilter> filters = new ArrayList<>();
        if (topics.length > 0) {
            filters.add(new TopicsFilter(topics));
        }
        if (beanNames.length > 0) {
            filters.addAll(
                    Stream.of(beanNames)
                            .map(beanName -> beanFactory.getBean(beanName, LogGroupFilter.class))
                            .collect(Collectors.toList())
            );
        }

        if (filters.size() == 0) {
            return new AcceptAllLogGroupFilter();
        } else {
            return new CompositeGroupFilter(filters);
        }

    }

    private LogFilter composeLogFilter(BeanFactory beanFactory) {
        String[] beanNames = annotation.logFilterBeanNames();
        if (beanNames.length == 0) {
            return new AcceptAllLogFilter();
        } else {
            return new CompositeLogFilter(
                    Stream.of(beanNames)
                            .map(beanName -> beanFactory.getBean(beanName, LogFilter.class))
                            .collect(Collectors.toList())
            );
        }
    }

    public Consumer<List<LogGroupData>> getLogGroupListener(BeanFactory beanFactory) {
        LogGroupFilter logGroupFilter = composeLogGroupFilter(beanFactory);
        LogFilter logFilter = composeLogFilter(beanFactory);

        return logGroupDataList -> {
            logGroupDataList.stream()
                    .filter(logGroupFilter::accept)
                    .map(logGroupData -> convert(logGroupData, annotation.target(), logFilter))
                    .flatMap(List::stream)
                    .forEach(this::invokeMethod);
        };
    }

    private <T> List<Message<T>> convert(LogGroupData logGroupData, Class<T> targetClass, LogFilter logFilter) {
        Logs.LogGroup logGroup = logGroupData.GetLogGroup();
        Map<String, String> headers = new HashMap<>();
        headers.put(LogGroupHeaders.SOURCE.getKey(), logGroup.getSource());
        headers.put(LogGroupHeaders.TOPIC.getKey(), logGroup.getTopic());
        headers.put(LogGroupHeaders.MACHINE_UUID.getKey(), logGroup.getMachineUUID());
        headers.put(LogGroupHeaders.CATEGORY.getKey(), logGroup.getCategory());

        return logGroup.getLogsList().stream()
                .filter(logFilter::accept)
                .map(log -> MessageBuilder
                        .withPayload(conversionService.convert(log, targetClass))
                        .copyHeaders(headers)
                        .build())
                .collect(Collectors.toList());
    }


}

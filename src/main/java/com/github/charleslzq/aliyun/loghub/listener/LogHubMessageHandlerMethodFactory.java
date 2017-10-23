package com.github.charleslzq.aliyun.loghub.listener;

import lombok.Setter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.messaging.converter.GenericMessageConverter;
import org.springframework.messaging.handler.annotation.support.*;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class LogHubMessageHandlerMethodFactory implements MessageHandlerMethodFactory, BeanFactoryAware {
    private MessageHandlerMethodFactory messageHandlerMethodFactory;
    @Setter
    private BeanFactory beanFactory;


    private MessageHandlerMethodFactory getMessageHandlerMethodFactory() {
        if (this.messageHandlerMethodFactory == null) {
            this.messageHandlerMethodFactory = this.createDefaultMessageHandlerMethodFactory();
        }

        return this.messageHandlerMethodFactory;
    }

    @Override
    public InvocableHandlerMethod createInvocableHandlerMethod(Object o, Method method) {
        return this.getMessageHandlerMethodFactory().createInvocableHandlerMethod(o, method);
    }

    private MessageHandlerMethodFactory createDefaultMessageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory defaultFactory = new DefaultMessageHandlerMethodFactory();
        defaultFactory.setBeanFactory(this.beanFactory);
        ConfigurableBeanFactory cbf = this.beanFactory instanceof ConfigurableBeanFactory ? (ConfigurableBeanFactory) this.beanFactory : null;
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
        defaultFactory.setConversionService(conversionService);
        List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<>();
        argumentResolvers.add(new HeaderMethodArgumentResolver(conversionService, cbf));
        argumentResolvers.add(new HeadersMethodArgumentResolver());
        final GenericMessageConverter messageConverter = new GenericMessageConverter(conversionService);
        argumentResolvers.add(new MessageMethodArgumentResolver(messageConverter));
        argumentResolvers.add(new PayloadArgumentResolver(messageConverter));
        defaultFactory.setArgumentResolvers(argumentResolvers);
        defaultFactory.afterPropertiesSet();
        return defaultFactory;
    }
}

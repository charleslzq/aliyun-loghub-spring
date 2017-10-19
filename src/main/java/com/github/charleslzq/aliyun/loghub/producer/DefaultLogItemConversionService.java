package com.github.charleslzq.aliyun.loghub.producer;

import com.aliyun.openservices.log.common.LogItem;
import com.google.gson.Gson;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

public class DefaultLogItemConversionService implements ConversionService {
    private Gson gson = new Gson();

    @Override
    public boolean canConvert(Class<?> aClass, Class<?> aClass1) {
        return aClass1.equals(LogItem.class);
    }

    @Override
    public boolean canConvert(TypeDescriptor typeDescriptor, TypeDescriptor typeDescriptor1) {
        return typeDescriptor1.getObjectType().equals(LogItem.class);
    }

    @Override
    public <T> T convert(Object o, Class<T> aClass) {
        if (aClass.equals(LogItem.class)) {
            return (T) createNewItem(o);
        } else {
            return null;
        }
    }

    @Override
    public Object convert(Object o, TypeDescriptor typeDescriptor, TypeDescriptor typeDescriptor1) {
        return createNewItem(o);
    }

    private LogItem createNewItem(Object target) {
        LogItem logItem = new LogItem();
        logItem.PushBack("content", gson.toJson(target));
        return logItem;
    }
}

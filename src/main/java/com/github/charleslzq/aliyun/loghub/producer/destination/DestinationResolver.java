package com.github.charleslzq.aliyun.loghub.producer.destination;

public interface DestinationResolver<T> {

    T resolveDestination(String destination);

}

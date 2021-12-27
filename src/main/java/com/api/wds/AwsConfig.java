package com.api.wds;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.client.builder.ExecutorFactory;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.annotation.support.PayloadArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author rohit
 * @Date 25/12/21
 **/
@Component
public class AwsConfig {

    private static final String DEFAULT_THREAD_NAME_PREFIX = ClassUtils.getShortName(SimpleMessageListenerContainer.class) + "-";
    private static final int CORE_POOL_SIZE = 6;
    private static final int MAX_POOL_SIZE = 12;

    @Value("${aws.sqs.region}")
    private String region;
    @Value("${aws.sqs.endpoint}")
    private String endpoint;

    @Bean
    public MappingJackson2MessageConverter jackson2Converter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        return converter;
    }

    @Bean
    public ClientConfiguration sqsClientConfiguration() {
        return new ClientConfiguration()
                .withConnectionTimeout(30000)
                .withRequestTimeout(30000)
                .withClientExecutionTimeout(30000);
    }

    @Bean
    public ExecutorFactory sqsExecutorFactory() {
        return () -> new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, 5L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    }

    @Bean
    @Primary
    public AmazonSQSAsync amazonSqs(ExecutorFactory sqsExecutorFactory) {
        AwsClientBuilder.EndpointConfiguration endpointConfiguration =
                new AwsClientBuilder.EndpointConfiguration(endpoint, region);
        return AmazonSQSAsyncClientBuilder.standard()
                .withExecutorFactory(sqsExecutorFactory)
                .withEndpointConfiguration(endpointConfiguration)
                .build();
    }

    @Bean
    public AsyncTaskExecutor queueContainerTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadNamePrefix(DEFAULT_THREAD_NAME_PREFIX);
        threadPoolTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        threadPoolTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        // No use of a thread pool executor queue to avoid retaining message to long in memory
        threadPoolTaskExecutor.setQueueCapacity(0);
        threadPoolTaskExecutor.afterPropertiesSet();
        return threadPoolTaskExecutor;

    }

    @Bean
    public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(AmazonSQSAsync amazonSqs,
                                                                                       AsyncTaskExecutor queueContainerTaskExecutor) {
        SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
        factory.setAmazonSqs(amazonSqs);
        factory.setAutoStartup(true);
        factory.setMaxNumberOfMessages(2);
        factory.setWaitTimeOut(20);
        factory.setTaskExecutor(queueContainerTaskExecutor);
        return factory;
    }

/*    @Bean
    public MappingJackson2MessageConverter mappingJackson2MessageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter jacksonMessageConverter = new MappingJackson2MessageConverter();
        jacksonMessageConverter.setObjectMapper(objectMapper);
        jacksonMessageConverter.setSerializedPayloadClass(String.class);
        jacksonMessageConverter.setStrictContentTypeMatch(true);
        return jacksonMessageConverter;
    }

    @Bean
    public QueueMessageHandlerFactory queueMessageHandlerFactory() {
        QueueMessageHandlerFactory factory = new QueueMessageHandlerFactory();
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();

        //set strict content type match to false
        messageConverter.setStrictContentTypeMatch(false);
        factory.setArgumentResolvers(Collections.<HandlerMethodArgumentResolver>singletonList(new PayloadArgumentResolver(messageConverter)));
        List<MessageConverter> mc = new ArrayList<>();
        mc.add(new MappingJackson2MessageConverter());
        factory.setMessageConverters(mc);
        return factory;
    }

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSQSAsync) {
        return new QueueMessagingTemplate(amazonSQSAsync);
    }*/

   /* @Bean
    public NotificationMessagingTemplate notificationMessagingTemplate(AmazonSNS amazonSNS) {
        return new NotificationMessagingTemplate(amazonSNS);
    }*/
}

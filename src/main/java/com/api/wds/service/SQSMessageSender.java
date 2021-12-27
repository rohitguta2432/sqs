package com.api.wds.service;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author rohit
 * @Date 25/12/21
 **/
@Component
public class SQSMessageSender {

    @Autowired
    private AmazonSQSAsync sqsClient;

    private QueueMessagingTemplate queueMessagingTemplate;

    public SQSMessageSender(AmazonSQSAsync sqsClient) {
        this.sqsClient = sqsClient;
        this.queueMessagingTemplate = new QueueMessagingTemplate(sqsClient);
    }

    public void send(String queue, Object message) {
        this.queueMessagingTemplate.convertAndSend(queue, message);
    }

}

package com.api.wds.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

/**
 * @Author rohit
 * @Date 24/12/21
 **/
@Service
@Slf4j
public class WdsService {

    @Autowired
    private SQSMessageSender sqsMessageSender;

    @Value("${aws.wds.queue}")
    private String wds;

    public String create() {
        sqsMessageSender.send(wds, new WdsMapper("test"));

        return "send successful";
    }

    @SqsListener(value = "${aws.wds.queue}")
    public void processQueue(WdsMapper queue) {
        log.info("Processing queue " + queue);
    }

}

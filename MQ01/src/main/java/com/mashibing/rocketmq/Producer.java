package com.mashibing.rocketmq;

import java.util.ArrayList;

import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * 消息发送者
 *
 * @author 一明哥
 */
public class Producer {

    public static void main(String[] args) throws Exception {

		DefaultMQProducer producer = new DefaultMQProducer("xoxogp");
        TransactionMQProducer producer = new TransactionMQProducer("xoxogp1");

        // 设置nameserver地址
        producer.setNamesrvAddr("192.168.0.110:9876");

        producer.setTransactionListener(new TransactionListener() {
            public LocalTransactionState executeLocalTransaction(Message message, Object o) {
                //执行本地事务
                System.out.println("--------------------------------executeLocalTransaction");
                System.out.println(message.getBody());
                System.out.println("--------------------------------executeLocalTransaction");

                return LocalTransactionState.UNKNOW;
            }

            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                // Broker 回调，检查事务

                System.out.println("--------------------------------checkLocalTransaction");
                System.out.println("--------------------------------checkLocalTransaction");
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });


        producer.start();

        TransactionSendResult result = producer.sendMessageInTransaction(new Message("xxoo001", "事务消息测试".getBytes()), null);

//        // topic 消息将要发送到的地址
//        // body  消息中的具体数据
//        Message msg1 = new Message("myTopic001", "xxxxooo 第一条".getBytes());
//        Message msg2 = new Message("myTopic001", "xxxxooo 第2条".getBytes());
//        Message msg3 = new Message("myTopic001", "xxxxooo 第3条".getBytes());
//
//        ArrayList<Message> list = new ArrayList<Message>();
//        list.add(msg1);
//        list.add(msg2);
//        list.add(msg3);
//
//        // 同步消息发送
//        // for
//        // list.add
//
//
//        SendResult sendResult3 = producer.send(list);
//
//        System.out.println(sendResult3);
//        producer.shutdown();
        System.out.println("已经停机");
        System.out.println(result);

    }
}

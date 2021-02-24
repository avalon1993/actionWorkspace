package com.mashibing.rocketmq;

import java.util.ArrayList;

import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * ��Ϣ������
 *
 * @author һ����
 */
public class Producer {

    public static void main(String[] args) throws Exception {

		DefaultMQProducer producer = new DefaultMQProducer("xoxogp");
        TransactionMQProducer producer = new TransactionMQProducer("xoxogp1");

        // ����nameserver��ַ
        producer.setNamesrvAddr("192.168.0.110:9876");

        producer.setTransactionListener(new TransactionListener() {
            public LocalTransactionState executeLocalTransaction(Message message, Object o) {
                //ִ�б�������
                System.out.println("--------------------------------executeLocalTransaction");
                System.out.println(message.getBody());
                System.out.println("--------------------------------executeLocalTransaction");

                return LocalTransactionState.UNKNOW;
            }

            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                // Broker �ص����������

                System.out.println("--------------------------------checkLocalTransaction");
                System.out.println("--------------------------------checkLocalTransaction");
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });


        producer.start();

        TransactionSendResult result = producer.sendMessageInTransaction(new Message("xxoo001", "������Ϣ����".getBytes()), null);

//        // topic ��Ϣ��Ҫ���͵��ĵ�ַ
//        // body  ��Ϣ�еľ�������
//        Message msg1 = new Message("myTopic001", "xxxxooo ��һ��".getBytes());
//        Message msg2 = new Message("myTopic001", "xxxxooo ��2��".getBytes());
//        Message msg3 = new Message("myTopic001", "xxxxooo ��3��".getBytes());
//
//        ArrayList<Message> list = new ArrayList<Message>();
//        list.add(msg1);
//        list.add(msg2);
//        list.add(msg3);
//
//        // ͬ����Ϣ����
//        // for
//        // list.add
//
//
//        SendResult sendResult3 = producer.send(list);
//
//        System.out.println(sendResult3);
//        producer.shutdown();
        System.out.println("�Ѿ�ͣ��");
        System.out.println(result);

    }
}

package me.saker.webflux.delayedtask.mq.active;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ScheduledMessage;

import javax.jms.*;

/**
 * activeMQ 延迟任务实现<br/>
 * http://activemq.apache.org/delay-and-schedule-message-delivery.html <br/>
 * 参考： https://www.jianshu.com/p/8caa6d66b10d <br/>
 *
 * @author 猎隼
 */
public class ActiveDelayedTaskTest {
    // tcp 地址
    public static final String BROKER_URL = "tcp://localhost:61616";
    // 目标，在ActiveMQ管理员控制台创建 http://localhost:8161/admin/queues.jsp
    public static final String DESTINATION = "delayed:task:queue";


    /**
     * 创建连接
     *
     * @return
     */
    private Connection newConnection() {
        try {
            // 创建连接工厂
            ConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, BROKER_URL);
            // 通过工厂创建一个连接
            Connection connection = factory.createConnection();
            // 启动连接
            connection.start();
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendMessage(Session session, MessageProducer producer) throws Exception {
        TextMessage message = session.createTextMessage("orderId=10001");
        //延迟时长
        message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 5000);
        //下一次延迟任务等待时间
        message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_PERIOD, 2000);
        //延迟任务重复次数
        message.setIntProperty(ScheduledMessage.AMQ_SCHEDULED_REPEAT, 5);
        producer.send(message);
    }

    /**
     * 消息生产者
     */
    public void produce() {
        try {
            //创建一个连接
            Connection connection = newConnection();
            // 创建一个session会话
            Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
            // 创建一个消息队列
            Destination destination = session.createQueue(DESTINATION);
            // 创建消息发送者
            MessageProducer producer = session.createProducer(destination);
            // 设置持久化模式
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            sendMessage(session, producer);
            //必须要加commit，否则不会持久化到文件
            session.commit();
            //释放资源
            session.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void consume() {
        try {
            //创建一个连接
            Connection connection = newConnection();
            // 创建一个session会话
            Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);

            // 创建一个消息队列
            Destination destination = session.createQueue(DESTINATION);
            // 创建消息发送者
            MessageConsumer consumer = session.createConsumer(destination);
            // 设置监听器
            consumer.setMessageListener(msg -> {
                if (msg != null) {
                    TextMessage tm = (TextMessage) msg;
                    try {
                        System.out.println("关闭订单:" + tm.getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });
            //等待生产者发送消息
            Thread.sleep(500000);
            //释放资源
            session.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            ActiveDelayedTaskTest test = new ActiveDelayedTaskTest();
            //test.produce();
            test.consume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

package uk.gov.ons.fwmt.fwmtrmadapter;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueNames;
import uk.gov.ons.fwmt.fwmtrmadapter.helper.TestReceiver;

@Configuration
public class IntegrationTestConfig {

  @Bean
  SimpleMessageListenerContainer testRequestContainer(ConnectionFactory connectionFactory,
      @Qualifier("testListenerAdapter") MessageListenerAdapter listenerAdapter) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.setQueueNames(QueueNames.ADAPTER_TO_JOBSVC_QUEUE,QueueNames.ADAPTER_TO_RM_QUEUE);
    container.setMessageListener(listenerAdapter);
    return container;
  }

  @Bean
  MessageListenerAdapter testListenerAdapter(TestReceiver receiver) {
    return new MessageListenerAdapter(receiver, "receiveMessage");
  }

}

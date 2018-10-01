package uk.gov.ons.fwmt.fwmtrmadapter.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ons.fwmt.fwmtrmadapter.message.impl.RMReceiverImpl;

@Configuration
public class RMQueueConfig {

  private static final String ACTION_FIELD_DLQ = "Action.FieldDLQ";
  private static final String ACTION_FIELD_QUEUE = "Action.Field";
  private static final String ACTION_FIELD_BINDING = "Action.Field.binding";

  // Queue
  @Bean
  public Queue rmToAdapterQueue() {
    return QueueBuilder.durable(ACTION_FIELD_QUEUE)
        .withArgument("x-dead-letter-exchange", "action-deadletter-exchange")
        .withArgument("x-dead-letter-routing-key", "Action.Field.binding")
        .build();
  }

  // DLQ
  @Bean
  Queue adapterDeadLetterQueue() {
    return QueueBuilder.durable(ACTION_FIELD_DLQ).build();
  }

  // Bindings
  @Bean
  public Binding rmToAdapterBinding(Queue rmToAdapterQueue, DirectExchange RMExchange) {
    return BindingBuilder.bind(rmToAdapterQueue).to(RMExchange)
        .with(ACTION_FIELD_BINDING);
  }

  // Listener
  @Bean
  public MessageListenerAdapter RMlistenerAdapter(RMReceiverImpl receiver) {
    return new MessageListenerAdapter(receiver, "receiveMessage");
  }

  // Exchange
  @Bean
  public DirectExchange RMExchange() {
    return new DirectExchange(ACTION_FIELD_BINDING);
  }

  // Container
  @Bean
  SimpleMessageListenerContainer RMcontainer(ConnectionFactory RMConnectionFactory,
      MessageListenerAdapter RMlistenerAdapter) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();

    container.setConnectionFactory(RMConnectionFactory);
    container.setQueueNames(ACTION_FIELD_QUEUE);
    container.setMessageListener(RMlistenerAdapter);
    return container;
  }

  // Connection Factory
  @Bean
  public ConnectionFactory RMConnectionFactory() {
    CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();

    String username = "guest";
    String password = "guest";
    String hostname = "localhost";
    int port = 6672;
    String virtualHost = "/";

    cachingConnectionFactory.setPort(port);
    cachingConnectionFactory.setHost(hostname);
    cachingConnectionFactory.setVirtualHost(virtualHost);
    cachingConnectionFactory.setPassword(password);
    cachingConnectionFactory.setUsername(username);

    return cachingConnectionFactory;
  }
}

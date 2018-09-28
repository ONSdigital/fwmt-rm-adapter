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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueNames;
import uk.gov.ons.fwmt.fwmtrmadapter.message.impl.RMReceiverImpl;

@Configuration
public class RMQueueConfig {

  private static final String ACTION_FIELD_DLQ = "Action.FieldDLQ";
  private static final String ACTION_FIELD_QUEUE = "Action.Field";
  private final String username;
  private final String password;
  private final String hostname;
  private final int port;
  private final String virtualHost;

  public RMQueueConfig(@Value("${spring.rabbitmq.username}") String username,
      @Value("${spring.rabbitmq.password}") String password,
      @Value("${spring.rabbitmq.hostname}") String hostname,
      @Value("${spring.rabbitmq.rm.port}") int port,
      @Value("${spring.rabbitmq.virtualhost}") String virtualHost) {

    this.username = username;
    this.password = password;
    this.hostname = hostname;
    this.port = port;
    this.virtualHost = virtualHost;

  }

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
  public Binding rmToAdapterBinding(Queue rmToAdapterQueue, DirectExchange exchange) {
    return BindingBuilder.bind(rmToAdapterQueue).to(exchange)
        .with(QueueNames.RM_REQUEST_ROUTING_KEY);
  }

  // Listener
  @Bean
  public MessageListenerAdapter RMlistenerAdapter(RMReceiverImpl receiver) {
    return new MessageListenerAdapter(receiver, "receiveMessage");
  }

  // Container
  @Bean
  SimpleMessageListenerContainer RMcontainer(ConnectionFactory RMConnectionFactory,
      MessageListenerAdapter RMlistenerAdapter) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();

    container.setConnectionFactory(RMConnectionFactory);
    container.setQueueNames(QueueNames.RM_TO_ADAPTER_QUEUE);
    container.setMessageListener(RMlistenerAdapter);
    return container;
  }

  // Connection Factory
  @Bean
  public ConnectionFactory RMConnectionFactory() {
    CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();

    cachingConnectionFactory.setPort(port);
    cachingConnectionFactory.setHost(hostname);
    cachingConnectionFactory.setVirtualHost(virtualHost);
    cachingConnectionFactory.setPassword(password);
    cachingConnectionFactory.setUsername(username);

    return cachingConnectionFactory;
  }
}

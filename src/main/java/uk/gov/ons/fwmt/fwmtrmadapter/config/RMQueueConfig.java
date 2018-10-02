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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ons.fwmt.fwmtrmadapter.message.impl.RMReceiverImpl;

@Configuration
public class RMQueueConfig {

  private int initialInterval;
  private int multiplier;
  private int maxInterval;
  private String username;
  private String password;
  private String hostname;
  private int rmPort;
  private String virtualHost;

  public RMQueueConfig(@Value("${rabbitmq.initialinterval}") int initialInterval,
      @Value("${rabbitmq.multiplier}") int multiplier,
      @Value("$rabbitmq.maxInterval") int maxInterval,
      @Value("$rabbitmq.username") String username,
      @Value("$rabbitmq.password") String password,
      @Value("$rabbitmq.hostname") String hostname,
      @Value("$rabbitmq.fwmtPort") int rmPort,
      @Value("$rabbitmq.virtualHost") String virtualHost) {
    this.initialInterval = initialInterval;
    this.multiplier = multiplier;
    this.maxInterval = maxInterval;
    this.username = username;
    this.password = password;
    this.hostname = hostname;
    this.rmPort = rmPort;
    this.virtualHost = virtualHost;
  }

  // Queue Names
  // TODO add to common
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

  // Dead Letter Queue
  @Bean
  Queue adapterDeadLetterQueue() {
    return QueueBuilder.durable(ACTION_FIELD_DLQ).build();
  }

  // Bindings
  @Bean
  public Binding rmToAdapterBinding(@Qualifier("rmToAdapterQueue") Queue queue,
      @Qualifier("rmExchange") DirectExchange directExchange) {
    return BindingBuilder.bind(queue).to(directExchange)
        .with(ACTION_FIELD_BINDING);
  }

  // Listener
  @Bean
  public MessageListenerAdapter rmListenerAdapter(RMReceiverImpl receiver) {
    return new MessageListenerAdapter(receiver, "receiveMessage");
  }

  // Exchange
  @Bean
  public DirectExchange rmExchange() {
    return new DirectExchange(ACTION_FIELD_BINDING);
  }

  // Container
  @Bean
  SimpleMessageListenerContainer rmContainer(@Qualifier("rmConnectionFactory") ConnectionFactory connectionFactory,
      @Qualifier("rmListenerAdapter") MessageListenerAdapter messageListenerAdapter) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();

    container.setConnectionFactory(connectionFactory);
    container.setQueueNames(ACTION_FIELD_QUEUE);
    container.setMessageListener(messageListenerAdapter);
    return container;
  }

  // Connection Factory
  @Bean
  public ConnectionFactory rmConnectionFactory() {
    CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();

    cachingConnectionFactory.setPort(rmPort);
    cachingConnectionFactory.setHost(hostname);
    cachingConnectionFactory.setVirtualHost(virtualHost);
    cachingConnectionFactory.setPassword(password);
    cachingConnectionFactory.setUsername(username);

    return cachingConnectionFactory;
  }
}

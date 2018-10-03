package uk.gov.ons.fwmt.fwmtrmadapter.config;

import org.aopalliance.aop.Advice;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import uk.gov.ons.fwmt.fwmtrmadapter.message.impl.RMReceiverImpl;

import static uk.gov.ons.fwmt.fwmtrmadapter.config.ConnectionFactoryBuilder.createConnectionFactory;

@Configuration
public class RMQueueConfig {

  // Queue Names
  // TODO add to common
  private static final String ACTION_FIELD_DLQ = "Action.FieldDLQ";
  private static final String ACTION_FIELD_QUEUE = "Action.Field";
  private static final String ACTION_FIELD_BINDING = "Action.Field.binding";

  private String username;
  private String password;
  private String hostname;
  private int rmPort;
  private String virtualHost;

  public RMQueueConfig(
      @Value("${rabbitmq.username}") String username,
      @Value("${rabbitmq.password}") String password,
      @Value("${rabbitmq.hostname}") String hostname,
      @Value("${rabbitmq.rmPort}") Integer rmPort,
      @Value("${rabbitmq.virtualHost}") String virtualHost) {
    this.username = username;
    this.password = password;
    this.hostname = hostname;
    this.rmPort = rmPort;
    this.virtualHost = virtualHost;
  }

  // Queue
  @Bean
  public Queue rmToAdapterQueue() {
    Queue queue = QueueBuilder.durable(ACTION_FIELD_QUEUE)
        .withArgument("x-dead-letter-exchange", "action-deadletter-exchange")
        .withArgument("x-dead-letter-routing-key", "Action.Field.binding")
        .build();
    queue.setAdminsThatShouldDeclare(rmAmqpAdmin());
    return queue;
  }

  // Dead Letter Queue
  @Bean
  public Queue adapterDeadLetterQueue() {
    Queue queue = QueueBuilder.durable(ACTION_FIELD_DLQ).build();
    queue.setAdminsThatShouldDeclare(rmAmqpAdmin());
    return queue;
  }

  // Bindings
  @Bean
  public Binding rmToAdapterBinding(@Qualifier("rmToAdapterQueue") Queue queue,
      @Qualifier("rmExchange") DirectExchange directExchange) {
    Binding binding = BindingBuilder.bind(queue).to(directExchange)
        .with(ACTION_FIELD_BINDING);
    binding.setAdminsThatShouldDeclare(rmAmqpAdmin());
    return binding;
  }

  // Listener
  @Bean
  public MessageListenerAdapter rmListenerAdapter(RMReceiverImpl receiver) {
    return new MessageListenerAdapter(receiver, "receiveMessage");
  }

  // Exchange
  @Bean
  public DirectExchange rmExchange() {
    DirectExchange exchange = new DirectExchange(ACTION_FIELD_BINDING);
    exchange.setAdminsThatShouldDeclare(rmAmqpAdmin());
    return exchange;
  }

  // Container
  @Bean
  public SimpleMessageListenerContainer rmContainer(
      @Qualifier("rmConnectionFactory") ConnectionFactory connectionFactory,
      @Qualifier("rmListenerAdapter") MessageListenerAdapter messageListenerAdapter,
      @Qualifier("interceptor") RetryOperationsInterceptor retryOperationsInterceptor) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();

    Advice[] adviceChain = {retryOperationsInterceptor};

    container.setAdviceChain(adviceChain);
    container.setConnectionFactory(connectionFactory);
    container.setQueueNames(ACTION_FIELD_QUEUE);
    container.setMessageListener(messageListenerAdapter);
    return container;
  }

  @Bean
  public AmqpAdmin rmAmqpAdmin() {
    return new RabbitAdmin(rmConnectionFactory());
  }

  // Connection Factory
  @Bean
  public ConnectionFactory rmConnectionFactory() {
    return createConnectionFactory(rmPort, hostname, virtualHost, password, username);
  }
}

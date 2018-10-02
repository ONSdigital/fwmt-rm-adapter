package uk.gov.ons.fwmt.fwmtrmadapter.config;

import org.aopalliance.aop.Advice;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.retry.RetryOperations;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueNames;
import uk.gov.ons.fwmt.fwmtgatewaycommon.retry.CustomMessageRecover;
import uk.gov.ons.fwmt.fwmtrmadapter.message.impl.JobServiceReceiverImpl;

import static uk.gov.ons.fwmt.fwmtrmadapter.config.ConnectionFactoryBuilder.createConnectionFactory;

@Configuration
public class FWMTQueueConfig {

  private String username;
  private String password;
  private String hostname;
  private int fwmtPort;
  private String virtualHost;

  public FWMTQueueConfig(
      @Value("${rabbitmq.username}") String username,
      @Value("${rabbitmq.password}") String password,
      @Value("${rabbitmq.hostname}") String hostname,
      @Value("${rabbitmq.fwmtPort}") Integer fwmtPort,
      @Value("${rabbitmq.virtualHost}") String virtualHost) {
    this.username = username;
    this.password = password;
    this.hostname = hostname;
    this.fwmtPort = fwmtPort;
    this.virtualHost = virtualHost;
  }

  // Queue
  @Bean
  public Queue adapterToJobSvcQueue() {
    return QueueBuilder.durable(QueueNames.ADAPTER_TO_JOBSVC_QUEUE)
        .withArgument("x-dead-letter-exchange", "")
        .withArgument("x-dead-letter-routing-key", QueueNames.ADAPTER_JOB_SVC_DLQ)
        .build();
  }

  @Bean
  public Queue jobSvcToAdapterQueue() {
    return QueueBuilder.durable(QueueNames.JOBSVC_TO_ADAPTER_QUEUE)
        .withArgument("x-dead-letter-exchange", "")
        .withArgument("x-dead-letter-routing-key", QueueNames.JOB_SVC_ADAPTER_DLQ)
        .build();
  }

  @Bean
  public Queue adapterToRmQueue() {
    return QueueBuilder.durable(QueueNames.ADAPTER_TO_RM_QUEUE)
        .withArgument("x-dead-letter-exchange", "")
        .withArgument("x-dead-letter-routing-key", QueueNames.ADAPTER_RM_DLQ)
        .build();
  }

  // Dead Letter Queues
  @Bean
  public Queue adapterDeadLetterQueue() {
    return QueueBuilder.durable(QueueNames.ADAPTER_JOB_SVC_DLQ).build();
  }

  @Bean
  public Queue jobSvsDeadLetterQueue() {
    return QueueBuilder.durable(QueueNames.JOB_SVC_ADAPTER_DLQ).build();
  }

  @Bean
  public Queue rmAdapterDeadLetterQueue() {
    return QueueBuilder.durable(QueueNames.RM_ADAPTER_DLQ).build();
  }

  @Bean
  public Queue adapterRmDeadLetterQueue() {
    return QueueBuilder.durable(QueueNames.ADAPTER_RM_DLQ).build();
  }

  // Exchange
  @Bean
  @Primary
  public DirectExchange fwmtExchange() {
    return new DirectExchange(QueueNames.RM_JOB_SVC_EXCHANGE);
  }

  // Bindings
  @Bean
  public Binding adapterToJobSvcBinding(@Qualifier("adapterToJobSvcQueue") Queue queue,
      @Qualifier("fwmtExchange") DirectExchange directExchange) {
    return BindingBuilder.bind(queue).to(directExchange)
        .with(QueueNames.JOB_SVC_REQUEST_ROUTING_KEY);
  }

  @Bean
  public Binding jobSvcToAdapterBinding(@Qualifier("jobSvcToAdapterQueue") Queue queue,
      @Qualifier("fwmtExchange") DirectExchange directExchange) {
    return BindingBuilder.bind(queue).to(directExchange)
        .with(QueueNames.JOB_SVC_RESPONSE_ROUTING_KEY);
  }

  @Bean
  public Binding adapterToRmBinding(@Qualifier("adapterToRmQueue") Queue queue,
      @Qualifier("fwmtExchange") DirectExchange directExchange) {
    return BindingBuilder.bind(queue).to(directExchange)
        .with(QueueNames.RM_RESPONSE_ROUTING_KEY);
  }

  // Listener
  @Bean
  public MessageListenerAdapter jobSvcListenerAdapter(JobServiceReceiverImpl receiver) {
    return new MessageListenerAdapter(receiver, "receiveMessage");
  }

  // Interceptor
  @Bean
  public RetryOperationsInterceptor interceptor(
      @Qualifier("retryTemplate") RetryOperations retryOperations) {
    RetryOperationsInterceptor interceptor = new RetryOperationsInterceptor();
    interceptor.setRecoverer(new CustomMessageRecover());
    interceptor.setRetryOperations(retryOperations);
    return interceptor;
  }

  // Container
  @Bean
  public SimpleMessageListenerContainer jobSvcContainer(
      @Qualifier("fwmtConnectionFactory") ConnectionFactory connectionFactory,
      @Qualifier("jobSvcListenerAdapter") MessageListenerAdapter messageListenerAdapter,
      @Qualifier("interceptor") RetryOperationsInterceptor retryOperationsInterceptor) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();

    Advice[] adviceChain = {retryOperationsInterceptor};

    container.setAdviceChain(adviceChain);
    container.setConnectionFactory(connectionFactory);
    container.setQueueNames(QueueNames.JOBSVC_TO_ADAPTER_QUEUE);
    container.setMessageListener(messageListenerAdapter);
    return container;
  }

  // Connection Factory
  @Bean
  @Primary
  public ConnectionFactory fwmtConnectionFactory() {
    return createConnectionFactory(fwmtPort, hostname, virtualHost, password, username);
  }

}

package uk.gov.ons.fwmt.fwmtrmadapter.config;

import org.aopalliance.aop.Advice;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.retry.RetryOperations;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.support.RetryTemplate;
import uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueNames;
import uk.gov.ons.fwmt.fwmtgatewaycommon.retry.CTPRetryPolicy;
import uk.gov.ons.fwmt.fwmtgatewaycommon.retry.CustomMessageRecover;
import uk.gov.ons.fwmt.fwmtrmadapter.message.impl.JobServiceReceiverImpl;
import uk.gov.ons.fwmt.fwmtrmadapter.retrysupport.DefaultListenerSupport;

@Configuration
public class FWMTQueueConfig {

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
  Queue adapterDeadLetterQueue() {
    return QueueBuilder.durable(QueueNames.ADAPTER_JOB_SVC_DLQ).build();
  }

  @Bean
  Queue jobSvsDeadLetterQueue() {
    return QueueBuilder.durable(QueueNames.JOB_SVC_ADAPTER_DLQ).build();
  }

  @Bean
  Queue rmAdapterDeadLetterQueue() {
    return QueueBuilder.durable(QueueNames.RM_ADAPTER_DLQ).build();
  }

  @Bean
  Queue adapterRmDeadLetterQueue() {
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
  RetryOperationsInterceptor interceptor(
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

  // Retry Template
  @Bean
  public RetryTemplate retryTemplate() {
    RetryTemplate retryTemplate = new RetryTemplate();

    ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
    backOffPolicy.setInitialInterval(5000);
    backOffPolicy.setMultiplier(3.0);
    backOffPolicy.setMaxInterval(45000);
    retryTemplate.setBackOffPolicy(backOffPolicy);

    CTPRetryPolicy ctpRetryPolicy = new CTPRetryPolicy();
    retryTemplate.setRetryPolicy(ctpRetryPolicy);

    retryTemplate.registerListener(new DefaultListenerSupport());

    return retryTemplate;
  }

  // Connection Factory
  @Bean
  @Primary
  public ConnectionFactory fwmtConnectionFactory() {
    CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();

    // TODO make environment variables
    String username = "guest";
    String password = "guest";
    String hostname = "localhost";
    int port = 5672;
    String virtualHost = "/";

    cachingConnectionFactory.setPort(port);
    cachingConnectionFactory.setHost(hostname);
    cachingConnectionFactory.setVirtualHost(virtualHost);
    cachingConnectionFactory.setPassword(password);
    cachingConnectionFactory.setUsername(username);

    return cachingConnectionFactory;
  }
}

package uk.gov.ons.fwmt.fwmtrmadapter.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import uk.gov.ons.ctp.common.retry.CTPRetryPolicy;
import uk.gov.ons.fwmt.fwmtrmadapter.message.impl.JobServiceReceiverImpl;
import uk.gov.ons.fwmt.fwmtrmadapter.message.impl.RMReceiverImpl;
import uk.gov.ons.fwmt.fwmtrmadapter.retrysupport.DefaultListenerSupport;

@Configuration
public class QueueConfig {
  private static final String ADAPTER_JOB_SVC_DLQ = "adapter-jobSvc.DLQ";
  private static final String JOB_SVC_ADAPTER_DLQ = "jobSvc-adapter.DLQ";
  private static final String RM_ADAPTER_DLQ = "rm-adapter.DLQ";
  private static final String ADAPTER_RM_DLQ = "adapter-rm.DLQ";

  @Bean
  public Queue rmToAdapterQueue() {
    return QueueBuilder.durable(uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueConfig.RM_TO_ADAPTER_QUEUE)
        .withArgument("x-dead-letter-exchange", "")
        .withArgument("x-dead-letter-routing-key", RM_ADAPTER_DLQ)
        .build();
  }

  @Bean
  public Queue adapterToJobSvcQueue() {
    return QueueBuilder.durable(uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueConfig.ADAPTER_TO_JOBSVC_QUEUE)
        .withArgument("x-dead-letter-exchange", "")
        .withArgument("x-dead-letter-routing-key", ADAPTER_JOB_SVC_DLQ)
        .build();
  }

  @Bean
  public Queue jobSvcToAdapterQueue() {
    return QueueBuilder.durable(uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueConfig.JOBSVC_TO_ADAPTER_QUEUE)
        .withArgument("x-dead-letter-exchange", "")
        .withArgument("x-dead-letter-routing-key", JOB_SVC_ADAPTER_DLQ)
        .build();
  }

  @Bean
  public Queue adapterToRMQueue() {
    return QueueBuilder.durable(uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueConfig.ADAPTER_TO_RM_QUEUE)
        .withArgument("x-dead-letter-exchange", "")
        .withArgument("x-dead-letter-routing-key", ADAPTER_RM_DLQ)
        .build();
  }

  @Bean
  Queue adapterDeadLetterQueue() {
    return QueueBuilder.durable(ADAPTER_JOB_SVC_DLQ).build();
  }

  @Bean
  Queue jobSvsDeadLetterQueue() {
    return QueueBuilder.durable(JOB_SVC_ADAPTER_DLQ).build();
  }

  @Bean
  Queue rmAdapterDeadLetterQueue() {
    return QueueBuilder.durable(RM_ADAPTER_DLQ).build();
  }

  @Bean
  Queue adapterRmDeadLetterQueue() {
    return QueueBuilder.durable(ADAPTER_RM_DLQ).build();
  }

  @Bean
  public TopicExchange exchange() {
    return new TopicExchange(uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueConfig.RM_JOB_SVC_EXCHANGE);
  }

  @Bean
  public Binding rmToAdapterBinding(@Qualifier("rmToAdapterQueue") Queue queue, TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange)
        .with(uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueConfig.RM_REQUEST_ROUTING_KEY);
  }

  @Bean
  public Binding adapterToJobSvcBinding(@Qualifier("adapterToJobSvcQueue") Queue queue, TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange)
        .with(uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueConfig.JOB_SVC_REQUEST_ROUTING_KEY);
  }

  @Bean
  public Binding jobSvcToAdapterBinding(@Qualifier("jobSvcToAdapterQueue") Queue queue, TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange)
        .with(uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueConfig.JOB_SVC_RESPONSE_ROUTING_KEY);
  }

  @Bean
  public Binding adapterToRMBinding(@Qualifier("adapterToRMQueue") Queue queue, TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange)
        .with(uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueConfig.RM_RESPONSE_ROUTING_KEY);
  }

  @Bean
  public MessageListenerAdapter listenerAdapter(RMReceiverImpl receiver) {
    return new MessageListenerAdapter(receiver, "receiveMessage");
  }

  @Bean
  public MessageListenerAdapter jobSvcListenerAdapter(JobServiceReceiverImpl receiver) {
    return new MessageListenerAdapter(receiver, "receiveMessage");
  }

  @Bean
  public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
      @Qualifier("listenerAdapter") MessageListenerAdapter listenerAdapter) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.setQueueNames(uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueConfig.RM_TO_ADAPTER_QUEUE);
    container.setMessageListener(listenerAdapter);
    return container;
  }

  @Bean
  public SimpleMessageListenerContainer jobSvcContainer(ConnectionFactory connectionFactory,
      @Qualifier("jobSvcListenerAdapter") MessageListenerAdapter listenerAdapter) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.setQueueNames(uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueConfig.JOBSVC_TO_ADAPTER_QUEUE);
    container.setMessageListener(listenerAdapter);
    return container;
  }

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
}

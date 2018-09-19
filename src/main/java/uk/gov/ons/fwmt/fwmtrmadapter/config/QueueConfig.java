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
import uk.gov.ons.fwmt.fwmtrmadapter.message.impl.JobServiceReceiverImpl;
import uk.gov.ons.fwmt.fwmtrmadapter.message.impl.RMReceiverImpl;

@Configuration
public class QueueConfig {
  private static final String DEAD_LETTER_QUEUE_NAME = "adapter-jobSvc.DLQ";

  @Bean
  public Queue rmToAdapterQueue() {
    return new Queue(uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueConfig.RM_TO_ADAPTER_QUEUE, true);
  }

  @Bean
  public Queue adapterToJobSvcQueue() {
    return QueueBuilder.durable(uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueConfig.ADAPTER_TO_JOBSVC_QUEUE)
        .withArgument("x-dead-letter-exchange", "")
        .withArgument("x-dead-letter-routing-key", DEAD_LETTER_QUEUE_NAME)
        .build();
  }

  @Bean
  public Queue jobSvcToAdapterQueue() {
    return new Queue(uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueConfig.JOBSVC_TO_ADAPTER_QUEUE, true);
  }

  @Bean
  public Queue adapterToRMQueue() {
    return new Queue(uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueConfig.ADAPTER_TO_RM_QUEUE, true);
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
}

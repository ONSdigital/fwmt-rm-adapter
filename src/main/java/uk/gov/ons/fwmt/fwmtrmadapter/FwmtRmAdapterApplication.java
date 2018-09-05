package uk.gov.ons.fwmt.fwmtrmadapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueConfig;
import uk.gov.ons.fwmt.fwmtrmadapter.message.impl.JobServiceReceiverImpl;
import uk.gov.ons.fwmt.fwmtrmadapter.message.impl.RMReceiverImpl;

@SpringBootApplication
public class FwmtRmAdapterApplication {

	@Bean
	public Queue rmToAdapterQueue() {
		return new Queue(QueueConfig.RM_TO_ADAPTER_QUEUE, false);
	}

	@Bean
	public Queue adapterToJobSvcQueue() {
		return new Queue(QueueConfig.ADAPTER_TO_JOBSVC_QUEUE, false);
	}

	@Bean
	public Queue jobSvcToAdapterQueue() {
		return new Queue(QueueConfig.JOBSVC_TO_ADAPTER_QUEUE, false);
	}

	@Bean
	public Queue adapterToRMQueue() {
		return new Queue(QueueConfig.ADAPTER_TO_RM_QUEUE, false);
	}

	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(QueueConfig.RM_JOB_SVC_EXCHANGE);
	}

	@Bean
	public Binding rmToAdapterBinding(@Qualifier("rmToAdapterQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(QueueConfig.RM_REQUEST_ROUTING_KEY);
	}

	@Bean
	public Binding adapterToJobSvcBinding(@Qualifier("adapterToJobSvcQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(QueueConfig.JOB_SVC_REQUEST_ROUTING_KEY);
	}

	@Bean
	public Binding jobSvcToAdapterBinding(@Qualifier("jobSvcToAdapterQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(QueueConfig.JOB_SVC_RESPONSE_ROUTING_KEY);
	}

	@Bean
	public Binding adapterToRMBinding(@Qualifier("adapterToRMQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(QueueConfig.RM_RESPONSE_ROUTING_KEY);
	}

	@Bean
	public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
			@Qualifier("listenerAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(QueueConfig.RM_TO_ADAPTER_QUEUE);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return  mapper;
	}

	@Bean
	public MessageListenerAdapter listenerAdapter(RMReceiverImpl receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}

	@Bean
	public SimpleMessageListenerContainer jobSvcContainer(ConnectionFactory connectionFactory,
			@Qualifier("jobSvcListenerAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(QueueConfig.JOBSVC_TO_ADAPTER_QUEUE);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	public MessageListenerAdapter jobSvcListenerAdapter(JobServiceReceiverImpl receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}


	public static void main(String[] args) {
		SpringApplication.run(FwmtRmAdapterApplication.class, args);
	}
}

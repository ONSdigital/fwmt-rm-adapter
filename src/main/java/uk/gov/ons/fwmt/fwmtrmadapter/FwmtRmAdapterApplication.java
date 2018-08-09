package uk.gov.ons.fwmt.fwmtrmadapter;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import uk.gov.ons.fwmt.fwmtrmadapter.message.impl.RMReceiverImpl;

@SpringBootApplication
public class FwmtRmAdapterApplication {

	//replace with RM names
	static final String topicExchangeName = "rm-create-exchange";

	static final String rmQueue = "rm-create";

	static final String jobSvcQueue = "job-svc-create";

	@Bean
	Queue RMQueue() {
		return new Queue(rmQueue, false);
	}

	@Bean
	Queue jobSvcQueue() {
		return new Queue(jobSvcQueue, false);
	}

	@Bean
	TopicExchange exchange() {
		return new TopicExchange(topicExchangeName);
	}

	@Bean
	Binding binding(@Qualifier("RMQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with("rm.job.request.#");
	}

	@Bean
	Binding jobSvcbinding(@Qualifier("jobSvcQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with("job.svc.job.request.#");
	}

	@Bean
	SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(rmQueue,jobSvcQueue);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	MessageListenerAdapter listenerAdapter(RMReceiverImpl receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}


	public static void main(String[] args) {
		SpringApplication.run(FwmtRmAdapterApplication.class, args);
	}
}

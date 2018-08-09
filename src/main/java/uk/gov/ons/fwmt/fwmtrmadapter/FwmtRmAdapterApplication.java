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
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import uk.gov.ons.fwmt.fwmtrmadapter.message.JobSvcReceiver;
import uk.gov.ons.fwmt.fwmtrmadapter.message.impl.JobServiceReceiverImpl;
import uk.gov.ons.fwmt.fwmtrmadapter.message.impl.RMReceiverImpl;

@SpringBootApplication
public class FwmtRmAdapterApplication {

	//replace with RM names
	static final String RMJobSvcExchange = "rm-jobsvc-exchange";

	static final String rmToAdapterQueue = "rm-adapter";

	static final String adapterToJobSvcQueue = "adapter-jobSvc";

	static final String jobSvcToAdapterQueue = "jobsvc-adapter";

	static final String adapterToRMQueue = "adapter-rm";

	@Bean
	Queue rmToAdapterQueue() {
		return new Queue(rmToAdapterQueue, false);
	}

	@Bean
	Queue adapterToJobSvcQueue() {
		return new Queue(adapterToJobSvcQueue, false);
	}

	@Bean
	Queue jobSvcToAdapterQueue() {
		return new Queue(jobSvcToAdapterQueue, false);
	}

	@Bean
	Queue adapterToRMQueue() {
		return new Queue(adapterToRMQueue, false);
	}

	@Bean
	TopicExchange exchange() {
		return new TopicExchange(RMJobSvcExchange);
	}

	@Bean
	Binding rmToAdapterBinding(@Qualifier("rmToAdapterQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with("rm.job.request.#");
	}

	@Bean
	Binding adapterToJobSvcBinding(@Qualifier("adapterToJobSvcQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with("job.svc.job.request.#");
	}

	@Bean
	Binding jobSvcToAdapterBinding(@Qualifier("jobSvcToAdapterQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with("rm.job.response.#");
	}

	@Bean
	@Qualifier("listenerAdapter")
	Binding adapterToRMBinding(@Qualifier("adapterToRMQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with("job.svc.job.response.#");
	}

	@Bean
	SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
			@Qualifier("listenerAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(rmToAdapterQueue);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	@Qualifier("listenerAdapter")
	MessageListenerAdapter listenerAdapter(RMReceiverImpl receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}

	@Bean
	SimpleMessageListenerContainer aContainer(ConnectionFactory connectionFactory,
			@Qualifier("aListenerAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(jobSvcToAdapterQueue);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	MessageListenerAdapter aListenerAdapter(JobServiceReceiverImpl receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}


	public static void main(String[] args) {
		SpringApplication.run(FwmtRmAdapterApplication.class, args);
	}
}

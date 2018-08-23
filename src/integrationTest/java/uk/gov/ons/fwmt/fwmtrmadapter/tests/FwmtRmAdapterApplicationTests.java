package uk.gov.ons.fwmt.fwmtrmadapter.tests;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueConfig;
import uk.gov.ons.fwmt.fwmtrmadapter.IntegrationTestConfig;
import uk.gov.ons.fwmt.fwmtrmadapter.helper.TestReceiver;

import javax.xml.bind.JAXBException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Component
@Slf4j
@Import({IntegrationTestConfig.class,TestReceiver.class})
public class FwmtRmAdapterApplicationTests {

	private final	String XML = "<ins:actionInstruction xmlns:ins=\"http://ons.gov.uk/ctp/response/action/message/instruction\"><actionCancel><actionId>5a9f4323</actionId><responseRequired>true</responseRequired><reason>deleted for test</reason></actionCancel></ins:actionInstruction>";
	private final String EXPECTED_REQUEST_MESSAGE_JSON = "{\"actionType\":\"Cancel\",\"jobIdentity\":\"5a9f4323\",\"reason\":\"deleted for test\"}";
	private final String JSON = "{\"identity\":\"test\"}";
	private final String EXPECTED_RESPONSE_MESSAGE_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><DummyRMReturn><identity>test</identity></DummyRMReturn>";

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Test
	public void testPathFromRMToJobSvc() throws InterruptedException {

		TestReceiver testReceiver = new TestReceiver();
		testReceiver.init();
		rabbitTemplate.convertAndSend(QueueConfig.RM_JOB_SVC_EXCHANGE, QueueConfig.RM_REQUEST_ROUTING_KEY, XML.getBytes());

		Thread.sleep(2000);
		assertEquals(1,TestReceiver.counter);
		assertTrue(TestReceiver.result.equals(EXPECTED_REQUEST_MESSAGE_JSON));

	}

	@Test
	public void testPathFromJobSvcToRM() throws InterruptedException {

		TestReceiver testReceiver = new TestReceiver();
		testReceiver.init();
		rabbitTemplate.convertAndSend(QueueConfig.RM_JOB_SVC_EXCHANGE, QueueConfig.JOB_SVC_RESPONSE_ROUTING_KEY, JSON);

		Thread.sleep(2000);
		assertEquals(1,TestReceiver.counter);
		assertEquals(EXPECTED_RESPONSE_MESSAGE_XML,TestReceiver.result);

	}

}

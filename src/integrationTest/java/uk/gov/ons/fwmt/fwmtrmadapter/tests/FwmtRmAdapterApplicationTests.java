package uk.gov.ons.fwmt.fwmtrmadapter.tests;

import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueNames;
import uk.gov.ons.fwmt.fwmtrmadapter.IntegrationTestConfig;
import uk.gov.ons.fwmt.fwmtrmadapter.helper.TestReceiver;

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

  private static final String ACTION_FIELD_QUEUE = "Action.Field";
  private static final String ACTION_FIELD_BINDING = "Action.Field.binding";

	@Test
  @Ignore("We no longer create this queue, test needs changing")
  // TODO rewrite test
	public void testPathFromRMToJobSvc() throws InterruptedException {

		TestReceiver testReceiver = new TestReceiver();
		testReceiver.init();
    rabbitTemplate.convertAndSend(ACTION_FIELD_QUEUE, ACTION_FIELD_BINDING, XML.getBytes());

		Thread.sleep(2000);
		assertEquals(1,TestReceiver.counter);
		assertTrue(TestReceiver.result.equals(EXPECTED_REQUEST_MESSAGE_JSON));
	}

	@Test
	public void testPathFromJobSvcToRM() throws InterruptedException {

		TestReceiver testReceiver = new TestReceiver();
		testReceiver.init();
		rabbitTemplate.convertAndSend(QueueNames.RM_JOB_SVC_EXCHANGE, QueueNames.JOB_SVC_RESPONSE_ROUTING_KEY, JSON);

		Thread.sleep(2000);
		assertEquals(1,TestReceiver.counter);
		assertEquals(EXPECTED_RESPONSE_MESSAGE_XML,TestReceiver.result);
	}
}

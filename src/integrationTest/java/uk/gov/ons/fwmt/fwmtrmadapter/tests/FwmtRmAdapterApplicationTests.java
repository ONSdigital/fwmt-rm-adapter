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



	@Autowired
	private RabbitTemplate rabbitTemplate;

	private void before() {

	}

	@Test
	public void testPathFromRMToJobSvc() throws JAXBException, InterruptedException {

		TestReceiver testReceiver = new TestReceiver();
		testReceiver.init();
		String xml = "<ins:actionInstruction xmlns:ins=\"http://ons.gov.uk/ctp/response/action/message/instruction\"><actionCancel><actionId>5a9f4323</actionId><responseRequired>true</responseRequired><reason>deleted for test</reason></actionCancel></ins:actionInstruction>";
		rabbitTemplate.convertAndSend("rm-jobsvc-exchange", "rm.job.request.create", xml.getBytes());

		Thread.sleep(2000);
		assertEquals(1,TestReceiver.counter);
		assertTrue(TestReceiver.result.equals("{\"actionType\":\"Cancel\",\"jobIdentity\":\"5a9f4323\",\"reason\":\"deleted for test\"}"));

	}

	@Test
	public void testPathFromJobSvcToRM() throws JAXBException, InterruptedException {

		TestReceiver testReceiver = new TestReceiver();
		testReceiver.init();
		String JSON = "{\"identity\":\"test\"}";
		rabbitTemplate.convertAndSend("rm-jobsvc-exchange", "job.svc.job.response.create", JSON);

		Thread.sleep(2000);
		assertEquals(1,TestReceiver.counter);
		assertEquals("{\"identity\":\"test\"}",TestReceiver.result);

	}

}

package uk.gov.ons.fwmt.fwmtrmadapter.message.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import uk.gov.ons.fwmt.fwmtrmadapter.data.DummyRMReturn;
import uk.gov.ons.fwmt.fwmtrmadapter.message.RMProducer;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RMProducerImplTest {

  @InjectMocks
  private RMProducerImpl rmProducer;

  @Mock
  private RabbitTemplate rabbitTemplate;

  @Mock
  private Exchange exchange;

  @Captor
  private ArgumentCaptor argumentCaptor;

  @Test
  public void sendJobRequestResponse() {

    DummyRMReturn rmReturn = new DummyRMReturn();
    rmReturn.setIdentity("testIdentity");
    String expectedResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><DummyRMReturn><identity>testIdentity</identity></DummyRMReturn>";
    when(exchange.getName()).thenReturn("rm-jobsvc-exchange");
    rmProducer.sendJobRequestResponse(rmReturn);

    verify(rabbitTemplate).convertAndSend(eq("rm-jobsvc-exchange"),eq("job.svc.job.response.response"), argumentCaptor.capture());
    String result = String.valueOf(argumentCaptor.getValue());

    assertEquals(expectedResult, result);

  }

}

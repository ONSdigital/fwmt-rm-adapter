package uk.gov.ons.fwmt.fwmtrmadapter.message.impl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueConfig;
import uk.gov.ons.fwmt.fwmtgatewaycommon.exceptions.ExceptionCode;
import uk.gov.ons.fwmt.fwmtgatewaycommon.exceptions.types.FWMTCommonException;
import uk.gov.ons.fwmt.fwmtrmadapter.data.DummyRMReturn;

import javax.xml.bind.JAXBException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
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

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void sendJobRequestResponse() {

    DummyRMReturn rmReturn = new DummyRMReturn();
    rmReturn.setIdentity("testIdentity");
    String expectedResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><DummyRMReturn><identity>testIdentity</identity></DummyRMReturn>";
    when(exchange.getName()).thenReturn(QueueConfig.RM_JOB_SVC_EXCHANGE);
    rmProducer.sendJobRequestResponse(rmReturn);

    verify(rabbitTemplate).convertAndSend(eq(QueueConfig.RM_JOB_SVC_EXCHANGE),eq(QueueConfig.RM_RESPONSE_ROUTING_KEY), argumentCaptor.capture());
    String result = String.valueOf(argumentCaptor.getValue());

    assertEquals(expectedResult, result);

  }

//  @Test(expected = FWMTCommonException.class)
//  public void sendBadJobRequestResponse() {
//
//    DummyRMReturn dummyRMReturn =  new DummyRMReturn();
//    dummyRMReturn.setIdentity("Test");
//
//    doThrow(JAXBException.class).when(rabbitTemplate).convertAndSend(eq("exchangeName"),eq("job.svc.job.response.response"),anyString());
//
//    rmProducer.sendJobRequestResponse(dummyRMReturn);
//
//  }

}

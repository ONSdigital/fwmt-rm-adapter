package uk.gov.ons.fwmt.fwmtrmadapter.message.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import uk.gov.ons.fwmt.fwmtgatewaycommon.FWMTCreateJobRequest;
import uk.gov.ons.fwmt.fwmtrmadapter.helper.FWMTMessageBuilder;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JobServiceProducerImplTest {

  @InjectMocks JobServiceProducerImpl jobServiceProducer;
  @Captor ArgumentCaptor argumentCaptor;
  String expectedJSON = "{\"jobIdentity\":\"testJobIdentity\",\"surveyType\":\"testSurveyType\",\"preallocatedJob\":false,\"mandatoryResourceAuthNo\":\"testMandatoryResourceAuthNo\",\"dueDate\":{\"year\":2000,\"month\":\"NOVEMBER\",\"era\":\"CE\",\"dayOfYear\":316,\"dayOfWeek\":\"SATURDAY\",\"leapYear\":true,\"dayOfMonth\":11,\"monthValue\":11,\"chronology\":{\"id\":\"ISO\",\"calendarType\":\"iso8601\"}},\"address\":{\"line1\":\"testLine1\",\"line2\":\"testLine2\",\"line3\":\"testLine3\",\"line4\":\"testLine4\",\"townName\":\"testTownName\",\"postCode\":\"testPostCode\",\"latitude\":1000.0,\"longitude\":1000.0}}";
  @Mock
  private RabbitTemplate rabbitTemplate;
  @Mock
  @Qualifier("adapterToJobSvcQueue")
  private Queue queue;
  @Mock
  private Exchange exchange;

  @Test
  public void sendCreateJobRequest() {
    //Given
    FWMTMessageBuilder fwmtMessageBuilder = new FWMTMessageBuilder();
    FWMTCreateJobRequest fwmtCreateJobRequest = fwmtMessageBuilder.buildFWMTCreateJobRequest();
    when(exchange.getName()).thenReturn("exchange");

    //When
    jobServiceProducer.sendCreateJobRequest(fwmtCreateJobRequest);

    //Then
    verify(rabbitTemplate).convertAndSend(eq("exchange"), eq("job.svc.job.request.create"), argumentCaptor.capture());
    String result = (String) argumentCaptor.getValue();
    verify(exchange).getName();

    assertEquals(expectedJSON, result);
  }
}
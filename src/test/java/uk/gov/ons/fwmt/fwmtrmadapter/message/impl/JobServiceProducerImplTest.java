package uk.gov.ons.fwmt.fwmtrmadapter.message.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueNames;
import uk.gov.ons.fwmt.fwmtgatewaycommon.data.FWMTCreateJobRequest;
import uk.gov.ons.fwmt.fwmtrmadapter.common.error.CTPException;
import uk.gov.ons.fwmt.fwmtrmadapter.helper.FWMTMessageBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JobServiceProducerImplTest {

  private final String expectedJSON = "{\"jobIdentity\":\"testJobIdentity\",\"surveyType\":\"testSurveyType\",\"preallocatedJob\":false,\"mandatoryResourceAuthNo\":\"testMandatoryResourceAuthNo\",\"dueDate\":{\"year\":2000,\"month\":\"NOVEMBER\",\"era\":\"CE\",\"dayOfYear\":316,\"dayOfWeek\":\"SATURDAY\",\"leapYear\":true,\"dayOfMonth\":11,\"monthValue\":11,\"chronology\":{\"id\":\"ISO\",\"calendarType\":\"iso8601\"}},\"address\":{\"line1\":\"testLine1\",\"line2\":\"testLine2\",\"line3\":\"testLine3\",\"line4\":\"testLine4\",\"townName\":\"testTownName\",\"postCode\":\"testPostCode\",\"latitude\":1000.0,\"longitude\":1000.0}}";
  @InjectMocks
  private JobServiceProducerImpl jobServiceProducer;

  @Mock
  private RabbitTemplate rabbitTemplate;

  @Mock
  private Exchange exchange;
  @Captor
  private ArgumentCaptor argumentCaptor;
  @Mock
  private ObjectMapper objectMapper;

  @Test
  public void sendMessage() throws JsonProcessingException, CTPException {
    //Given
    FWMTMessageBuilder fwmtMessageBuilder = new FWMTMessageBuilder();
    FWMTCreateJobRequest fwmtCreateJobRequest = fwmtMessageBuilder.buildFWMTCreateJobRequest();
    when(exchange.getName()).thenReturn("exchange");
    when(objectMapper.writeValueAsString(eq(fwmtCreateJobRequest))).thenReturn(expectedJSON);

    //When
    jobServiceProducer.sendMessage(fwmtCreateJobRequest);

    //Then
    verify(rabbitTemplate).convertAndSend(eq("exchange"), eq(QueueNames.JOB_SVC_REQUEST_ROUTING_KEY), argumentCaptor.capture());
    String result = String.valueOf(argumentCaptor.getValue());

    assertEquals(expectedJSON, result);
  }

  @Test
  public void convertToJSON() throws JsonProcessingException, CTPException {
    //Given
    FWMTMessageBuilder fwmtMessageBuilder = new FWMTMessageBuilder();
    FWMTCreateJobRequest fwmtCreateJobRequest = fwmtMessageBuilder.buildFWMTCreateJobRequest();
    when(objectMapper.writeValueAsString(eq(fwmtCreateJobRequest))).thenReturn(anyString());

    System.out.println(fwmtCreateJobRequest.toString());

    //When
    String JSONResponce;
    JSONResponce = jobServiceProducer.convertToJSON(fwmtCreateJobRequest);

    //Then
    assertNotNull(JSONResponce);
  }

  @Test(expected = CTPException.class)
  public void sendBadMessage() throws JsonProcessingException, CTPException {
    //Given
    FWMTMessageBuilder fwmtMessageBuilder = new FWMTMessageBuilder();
    FWMTCreateJobRequest fwmtCreateJobRequest = fwmtMessageBuilder.buildFWMTCreateJobRequest();
    when(objectMapper.writeValueAsString(eq(fwmtCreateJobRequest))).thenThrow(new JsonProcessingException("Error"){});

    //When
    jobServiceProducer.sendMessage(fwmtCreateJobRequest);

  }
}
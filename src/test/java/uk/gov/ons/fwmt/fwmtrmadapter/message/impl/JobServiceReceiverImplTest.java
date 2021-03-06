package uk.gov.ons.fwmt.fwmtrmadapter.message.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ons.fwmt.fwmtgatewaycommon.data.DummyTMResponse;
import uk.gov.ons.fwmt.fwmtgatewaycommon.error.CTPException;
import uk.gov.ons.fwmt.fwmtohsjobstatusnotification.FwmtOHSJobStatusNotification;
import uk.gov.ons.fwmt.fwmtrmadapter.service.RMAdapterService;

import java.io.IOException;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JobServiceReceiverImplTest {

  @InjectMocks
  private JobServiceReceiverImpl jobServiceReceiver;

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private RMAdapterService rmAdapterService;

  @Test
  public void receiveMessage() throws IOException, CTPException {
    //Given
    String testReturnXML = "returnXML";
    FwmtOHSJobStatusNotification fwmtOHSJobStatusNotification = new FwmtOHSJobStatusNotification();
    when(objectMapper.readValue(eq(testReturnXML), eq(FwmtOHSJobStatusNotification.class))).thenReturn(fwmtOHSJobStatusNotification);

    //When
    jobServiceReceiver.receiveMessage(testReturnXML);

    //Then
    verify(rmAdapterService).returnJobRequest(fwmtOHSJobStatusNotification);
  }

  @Test(expected = CTPException.class)
  public void receiveMessageBadJson() throws IOException, CTPException {
    //Given
    String testReturnXML = "returnXML";
    when(objectMapper.readValue(eq(testReturnXML), eq(FwmtOHSJobStatusNotification.class))).thenThrow(new IOException());

    //When
    jobServiceReceiver.receiveMessage(testReturnXML);

    fail("Exception was not thrown");
  }
}
package uk.gov.ons.fwmt.fwmtrmadapter.message.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.fwmt.fwmtgatewaycommon.data.DummyTMResponse;
import uk.gov.ons.fwmt.fwmtrmadapter.message.JobSvcReceiver;
import uk.gov.ons.fwmt.fwmtrmadapter.service.RMAdapterService;

import java.io.IOException;

@Slf4j
@Component
public class JobServiceReceiverImpl implements JobSvcReceiver {


  @Autowired
  private RMAdapterService rmAdapterService;

  @Autowired
  private ObjectMapper objectMapper;

  @Retryable
  public void receiveMessage(String returnJobRequestXML) throws CTPException {
    try {
      final DummyTMResponse response = objectMapper.readValue(returnJobRequestXML, DummyTMResponse.class);
      rmAdapterService.returnJobRequest(response);

    } catch (IOException e) {
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, "Failed to map response.", e);
    }
  }
}

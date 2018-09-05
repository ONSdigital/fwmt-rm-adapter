package uk.gov.ons.fwmt.fwmtrmadapter.message.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.ons.fwmt.fwmtgatewaycommon.data.DummyTMResponse;
import uk.gov.ons.fwmt.fwmtgatewaycommon.exceptions.ExceptionCode;
import uk.gov.ons.fwmt.fwmtgatewaycommon.exceptions.types.FWMTCommonException;
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

  public void receiveMessage(String returnJobRequestXML) {

    log.info("Received Message:{}", returnJobRequestXML);

    try {
      final DummyTMResponse response = objectMapper.readValue(returnJobRequestXML, DummyTMResponse.class);
      rmAdapterService.returnJobRequest(response);

    } catch (IOException e) {
      throw new FWMTCommonException(ExceptionCode.INVALID_XML,"The XML received could not be marshalled",e);
    }
  }
}

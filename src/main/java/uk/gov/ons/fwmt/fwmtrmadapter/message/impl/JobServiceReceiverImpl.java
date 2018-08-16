package uk.gov.ons.fwmt.fwmtrmadapter.message.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.fwmt.fwmtgatewaycommon.DummyTMResponse;
import uk.gov.ons.fwmt.fwmtrmadapter.message.JobSvcReceiver;
import uk.gov.ons.fwmt.fwmtrmadapter.service.RMAdapterService;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Slf4j
@Component
public class JobServiceReceiverImpl implements JobSvcReceiver {


  @Autowired
  RMAdapterService rmAdapterService;
  @Autowired
  ObjectMapper objectMapper;

  public void receiveMessage(String returnJobRequestXML) {

    final String returnJobRequestXMLStr = new String(returnJobRequestXML);
    log.info("Received Message:{}",returnJobRequestXMLStr);

    try {
      final DummyTMResponse response = objectMapper.readValue(returnJobRequestXMLStr, DummyTMResponse.class);
      rmAdapterService.returnJobRequest(response);

    } catch (IOException e) {
      log.error("Error:", e);

    }
  }
}

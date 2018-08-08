package uk.gov.ons.fwmt.fwmtrmadapter.message.impl;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.fwmt.fwmtrmadapter.message.RMReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.ons.fwmt.fwmtrmadapter.service.RMAdapterService;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

@Component
@Slf4j
public class RMReceiverImpl implements RMReceiver {

  @Autowired
  RMAdapterService rmAdapterService;

  public void receiveMessage(String createJobRequestXML) throws JAXBException {
    log.debug(createJobRequestXML);
    JAXBContext jaxbContext = JAXBContext.newInstance(ActionInstruction.class);
    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

    StringReader reader = new StringReader(createJobRequestXML);
    ActionInstruction rmActionInstruction = (ActionInstruction) unmarshaller.unmarshal(reader);
    rmAdapterService.sendJobRequest(rmActionInstruction);

  }
}


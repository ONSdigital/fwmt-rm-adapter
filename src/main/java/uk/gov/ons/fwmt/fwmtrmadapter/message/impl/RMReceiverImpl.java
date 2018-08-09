package uk.gov.ons.fwmt.fwmtrmadapter.message.impl;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.fwmt.fwmtrmadapter.message.RMReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.ons.fwmt.fwmtrmadapter.service.RMAdapterService;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamResult;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
@Slf4j
public class RMReceiverImpl implements RMReceiver {

  @Autowired
  RMAdapterService rmAdapterService;

  public void receiveMessage(byte[] createJobRequestXML) throws JAXBException {
    JAXBContext jaxbContext = JAXBContext.newInstance(ActionInstruction.class);
    Marshaller marshaller = jaxbContext.createMarshaller();
    FileOutputStream os = null;
    try {
      os = new FileOutputStream("/Users/wardlk/Development/Tools/rm-tools/rabbity/new.xml");
      marshaller.marshal(new JAXBElement<ActionInstruction>(new QName("uri","local"),ActionInstruction, new StreamResult(os));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } finally {
      if (os != null) {
        try {
          os.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    log.info(new String(createJobRequestXML));
//    JAXBContext jaxbContext = JAXBContext.newInstance(ActionInstruction.class);
//    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
//
//    ByteArrayInputStream input = new ByteArrayInputStream(createJobRequestXML);
//    ActionInstruction rmActionInstruction = (ActionInstruction) unmarshaller.unmarshal(input);
    //    rmAdapterService.sendJobRequest(rmActionInstruction);

  }
}


package uk.gov.ons.fwmt.fwmtrmadapter.message;

import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;

public interface RMReceiver {

  void receiveMessage(byte[] createJobRequestXML) throws JAXBException;
}

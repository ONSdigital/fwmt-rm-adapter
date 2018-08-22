package uk.gov.ons.fwmt.fwmtrmadapter.message;

import javax.xml.bind.JAXBException;

public interface RMReceiver {

  void receiveMessage(byte[] createJobRequestXML) throws JAXBException;
}

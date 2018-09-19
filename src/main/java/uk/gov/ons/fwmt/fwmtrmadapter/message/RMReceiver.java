package uk.gov.ons.fwmt.fwmtrmadapter.message;

import uk.gov.ons.ctp.common.error.CTPException;

import javax.xml.bind.JAXBException;

public interface RMReceiver {

  void receiveMessage(byte[] createJobRequestXML) throws JAXBException, CTPException;
}

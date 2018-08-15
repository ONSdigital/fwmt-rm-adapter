package uk.gov.ons.fwmt.fwmtrmadapter.message;

import javax.xml.bind.JAXBException;

public interface JobSvcReceiver {

  void receiveMessage(String returnJobRequestXML);
}

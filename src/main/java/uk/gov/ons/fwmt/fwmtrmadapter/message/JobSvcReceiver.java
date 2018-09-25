package uk.gov.ons.fwmt.fwmtrmadapter.message;

import uk.gov.ons.fwmt.fwmtrmadapter.common.error.CTPException;

public interface JobSvcReceiver {

  void receiveMessage(String returnJobRequestXML) throws CTPException;
}

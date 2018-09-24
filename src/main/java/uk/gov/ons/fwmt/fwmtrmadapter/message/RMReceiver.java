package uk.gov.ons.fwmt.fwmtrmadapter.message;

import uk.gov.ons.fwmt.fwmtrmadapter.common.error.CTPException;

public interface RMReceiver {

  void receiveMessage(byte[] createJobRequestXML) throws CTPException;
}

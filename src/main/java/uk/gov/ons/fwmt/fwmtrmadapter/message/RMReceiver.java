package uk.gov.ons.fwmt.fwmtrmadapter.message;

import uk.gov.ons.fwmt.fwmtgatewaycommon.error.CTPException;

public interface RMReceiver {

  void receiveMessage(String createJobRequestXML) throws CTPException;
}

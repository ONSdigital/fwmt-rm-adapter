package uk.gov.ons.fwmt.fwmtrmadapter.message;

public interface JobSvcReceiver {

  void receiveMessage(String returnJobRequestXML);
}

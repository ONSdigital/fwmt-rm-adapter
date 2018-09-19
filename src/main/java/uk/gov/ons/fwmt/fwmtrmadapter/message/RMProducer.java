package uk.gov.ons.fwmt.fwmtrmadapter.message;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.fwmt.fwmtrmadapter.data.DummyRMReturn;

public interface RMProducer {

  void sendJobRequestResponse(DummyRMReturn createJobRequest) throws CTPException;
}

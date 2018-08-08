package uk.gov.ons.fwmt.fwmtrmadapter.message;

import uk.gov.ons.fwmt.fwmtgatewaycommon.FWMTCreateJobRequest;

public interface JobServiceProducer {

  void sendCreateJobRequest(FWMTCreateJobRequest createJobRequest);
}

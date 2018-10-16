package uk.gov.ons.fwmt.fwmtrmadapter.message;

import uk.gov.ons.fwmt.fwmtgatewaycommon.error.CTPException;
import uk.gov.ons.fwmt.fwmtohsjobstatusnotification.FwmtOHSJobStatusNotification;

public interface RMProducer {

  void sendJobRequestResponse(FwmtOHSJobStatusNotification createJobRequest) throws CTPException;
}

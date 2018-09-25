package uk.gov.ons.fwmt.fwmtrmadapter.message;

import uk.gov.ons.fwmt.fwmtrmadapter.common.error.CTPException;

public interface JobServiceProducer {
  void sendMessage(Object klass) throws CTPException;
}

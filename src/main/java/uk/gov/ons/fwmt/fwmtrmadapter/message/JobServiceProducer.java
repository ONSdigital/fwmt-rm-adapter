package uk.gov.ons.fwmt.fwmtrmadapter.message;

public interface JobServiceProducer {
  <T> void sendMessage(Object klass);
}

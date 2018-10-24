package uk.gov.ons.fwmt.fwmtrmadapter.message.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueNames;
import uk.gov.ons.fwmt.fwmtgatewaycommon.error.CTPException;
import uk.gov.ons.fwmt.fwmtrmadapter.message.JobServiceProducer;

@Slf4j
@Component
public class JobServiceProducerImpl implements JobServiceProducer {

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private Exchange exchange;

  @Autowired
  private ObjectMapper objectMapper;

  @Retryable
  public void sendMessage(Object dto) throws CTPException {
    String JSONJobRequest = convertToJSON(dto);
    rabbitTemplate.convertAndSend(exchange.getName(), QueueNames.JOB_SVC_REQUEST_ROUTING_KEY, JSONJobRequest);
    log.info("Message send to queue");
  }

  protected String convertToJSON(Object dto) throws CTPException {
    String JSONJobRequest;
    try {
      JSONJobRequest = objectMapper.writeValueAsString(dto);
    } catch (JsonProcessingException e) {
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, "Failed to process JSON.", e);
    }
    return JSONJobRequest;
  }
}

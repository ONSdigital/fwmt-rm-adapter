package uk.gov.ons.fwmt.fwmtrmadapter.message.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueConfig;
import uk.gov.ons.fwmt.fwmtgatewaycommon.exceptions.ExceptionCode;
import uk.gov.ons.fwmt.fwmtgatewaycommon.exceptions.types.FWMTCommonException;
import uk.gov.ons.fwmt.fwmtrmadapter.message.JobServiceProducer;

@Slf4j
@Component
public class JobServiceProducerImpl implements JobServiceProducer {

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  @Qualifier("adapterToJobSvcQueue")
  private Queue queue;

  @Autowired
  private Exchange exchange;

  @Autowired
  private ObjectMapper objectMapper;


  public void sendMessage(Object dto) {
    try {
      String JSONJobRequest = convertToJSON(dto);
      rabbitTemplate.convertAndSend(exchange.getName(), QueueConfig.JOB_SVC_REQUEST_ROUTING_KEY, JSONJobRequest);
    } catch(JsonProcessingException e) {
      throw new FWMTCommonException(ExceptionCode.UNABLE_TO_MAP_JSON,"Object could not be mapped to JSON",e);
    }
    log.info("Message send to queue", dto);
  }

  protected String convertToJSON(Object dto) throws JsonProcessingException {
    String JSONJobRequest = objectMapper.writeValueAsString(dto);
    log.info("CreateJobRequest: " + JSONJobRequest);
    return JSONJobRequest;
  }
}

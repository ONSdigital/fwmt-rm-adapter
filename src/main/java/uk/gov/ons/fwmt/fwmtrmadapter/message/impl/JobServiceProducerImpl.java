package uk.gov.ons.fwmt.fwmtrmadapter.message.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Exchange;
import org.springframework.beans.factory.annotation.Qualifier;
import uk.gov.ons.fwmt.fwmtrmadapter.message.JobServiceProducer;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.ons.fwmt.fwmtgatewaycommon.FWMTCreateJobRequest;

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

  public void sendCreateJobRequest(FWMTCreateJobRequest createJobRequest) {
    try {
      String JSONJobRequest = convertToJSON(createJobRequest);
      rabbitTemplate.convertAndSend(exchange.getName(), "job.svc.job.request.create", JSONJobRequest);
    } catch(JsonProcessingException e) {
      e.printStackTrace();
    }
    log.info("Done!");

  }

  private String convertToJSON(FWMTCreateJobRequest createJobRequest) throws JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper();
    String JSONJobRequest = mapper.writeValueAsString(createJobRequest);
    log.info(JSONJobRequest);
    return JSONJobRequest;
  }
}

package uk.gov.ons.fwmt.fwmtrmadapter.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueConfig;
import uk.gov.ons.fwmt.fwmtrmadapter.common.error.CTPException;
import uk.gov.ons.fwmt.fwmtrmadapter.data.DummyRMReturn;
import uk.gov.ons.fwmt.fwmtrmadapter.message.RMProducer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

@Component
@Slf4j
public class RMProducerImpl implements RMProducer {

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private Exchange exchange;

  @Retryable
  public void sendJobRequestResponse(DummyRMReturn dummyRMReturn) throws CTPException {
    JAXBContext jaxbContext;
    try {
      jaxbContext = JAXBContext.newInstance(DummyRMReturn.class);
      Marshaller marshaller = jaxbContext.createMarshaller();

      StringWriter sw = new StringWriter();
      marshaller.marshal(dummyRMReturn, sw);
      String rmJobRequestResponse = sw.toString();

      rabbitTemplate.convertAndSend(exchange.getName(), QueueConfig.RM_RESPONSE_ROUTING_KEY, rmJobRequestResponse);
    } catch (JAXBException e) {
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, "Failed to convert and send to RM.", e);
    }
  }
}

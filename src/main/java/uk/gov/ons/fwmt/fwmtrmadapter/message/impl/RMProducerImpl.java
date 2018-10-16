package uk.gov.ons.fwmt.fwmtrmadapter.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import uk.gov.ons.fwmt.fwmtgatewaycommon.config.QueueNames;
import uk.gov.ons.fwmt.fwmtgatewaycommon.error.CTPException;
import uk.gov.ons.fwmt.fwmtohsjobstatusnotification.FwmtOHSJobStatusNotification;
import uk.gov.ons.fwmt.fwmtrmadapter.message.RMProducer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.StringWriter;

@Component
@Slf4j
public class RMProducerImpl implements RMProducer {

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private Exchange exchange;

  @Retryable
  public void sendJobRequestResponse(FwmtOHSJobStatusNotification fwmtOHSJobStatusNotification) throws CTPException {
    JAXBContext jaxbContext;
    try {
      jaxbContext = JAXBContext.newInstance(FwmtOHSJobStatusNotification.class);
      Marshaller marshaller = jaxbContext.createMarshaller();

      StringWriter sw = new StringWriter();

      QName qName = new QName("http://ons.gov.uk/fwmt/FwmtOHSJobStatusNotification", "FwmtOHSJobStatusNotification");
      JAXBElement<FwmtOHSJobStatusNotification> root = new JAXBElement<FwmtOHSJobStatusNotification>(qName, FwmtOHSJobStatusNotification.class, fwmtOHSJobStatusNotification);
      marshaller.marshal(root, sw);
      String rmJobRequestResponse = sw.toString();

      rabbitTemplate.convertAndSend(exchange.getName(), QueueNames.RM_RESPONSE_ROUTING_KEY, rmJobRequestResponse);
      log.info("Sent job request to Job service");
    } catch (JAXBException e) {
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, "Failed to convert and send to RM.", e);
    }
  }
}

package uk.gov.ons.fwmt.fwmtrmadapter.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
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
  @Qualifier("adapterToRMQueue")
  private Queue queue;

  @Autowired
  private Exchange exchange;

  public void sendJobRequestResponse(DummyRMReturn dummyRMReturn) {
    JAXBContext jaxbContext = null;
    try {
      jaxbContext = JAXBContext.newInstance(DummyRMReturn.class);
      Marshaller marshaller = jaxbContext.createMarshaller();

      StringWriter sw = new StringWriter();
      marshaller.marshal(dummyRMReturn, sw);
      String rmJobRequestResponse = sw.toString();

      rabbitTemplate.convertAndSend(exchange.getName(), "job.svc.job.response.response", rmJobRequestResponse);
      log.info(new String("POSTING TO RM" +rmJobRequestResponse));

    } catch (JAXBException e) {
      e.printStackTrace();
    }


  }



}

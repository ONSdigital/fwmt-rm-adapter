package uk.gov.ons.fwmt.fwmtrmadapter.controller;

import java.util.Properties;

//github.com/ONSdigital/fwmt-rm-adapter.git
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/rabbitHealth")
public class RabbitHealthCheckController {

  @Autowired
  @Qualifier("rmConnectionFactory")
  private ConnectionFactory rmFactory;

  @Autowired
  @Qualifier("fwmtConnectionFactory")
  private ConnectionFactory fwmtConnectionFactory;

  @RequestMapping(value = "/queue", method = RequestMethod.GET, produces = "application/json")
  public boolean canAccessQueue(@RequestParam("qname") String qname) {
    ConnectionFactory cf = (qname.contains("Action.Field")) ? rmFactory : fwmtConnectionFactory;
    RabbitAdmin rabbitAdmin = new RabbitAdmin(cf);

    Properties queueProperties = rabbitAdmin.getQueueProperties(qname);
    return (queueProperties!=null && qname.equals(queueProperties.getProperty("QUEUE_NAME")));
  }
}

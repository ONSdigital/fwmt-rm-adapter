package uk.gov.ons.fwmt.fwmtrmadapter.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;

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
    ConnectionFactory cf = ("Action.Field".equals(qname))? rmFactory : fwmtConnectionFactory;
    RabbitAdmin rabbitAdmin = new RabbitAdmin(cf);
    Properties queueProperties = rabbitAdmin.getQueueProperties(qname);
    if (queueProperties == null) {
      return false;
    }
    String result1 = queueProperties.getProperty("QUEUE_NAME");
    if (qname.equals(result1)) {
      return true;
    }
    return false;
  }
}

package uk.gov.ons.fwmt.fwmtrmadapter.controller;


import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.RestController;

import uk.gov.ons.fwmt.fwmtrmadapter.config.RMQueueConfig;



@Slf4j
@RestController
public class RabbitHealthCheckController {

  private static final String ACTION_FIELD_QUEUE = "Action.Field";
  private static final String ACTION_FIELD_BINDING = "Action.Field.binding";

  @Value("${rabbitmq.rm.username}") String rmUsername;
  @Value("${rabbitmq.rm.password}") String rmPassword;
  @Value("${rabbitmq.rm.hostname}") String rmHostname;
  @Value("${rabbitmq.rm.port}") Integer rmPort;
  @Value("${rabbitmq.rm.virtualHost}") String virtualHost;

  private CachingConnectionFactory getRMConnectionFactory() {
    CachingConnectionFactory factory = new CachingConnectionFactory();
    factory.setHost(rmHostname);
    factory.setUsername(rmUsername);
    factory.setPassword(rmPassword);
    factory.setPort(rmPort);
    factory.setVirtualHost(virtualHost);
    System.out.println(rmUsername+" "+ rmPassword+" "+rmHostname+" "+rmPort);
    return factory;
  }


  @RequestMapping(value = "/rabbitHealth", method = RequestMethod.GET, produces = "application/json")
  public boolean rabbitHealth(){

    RabbitAdmin rabbitAdmin = new RabbitAdmin(getRMConnectionFactory());

    String result1 = rabbitAdmin.getQueueProperties(ACTION_FIELD_QUEUE).getProperty("QUEUE_NAME");

    if(result1.equals("Action.Field")){
      return true;
    }

    return false;
  }


}

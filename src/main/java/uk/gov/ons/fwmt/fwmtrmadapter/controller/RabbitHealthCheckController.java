package uk.gov.ons.fwmt.fwmtrmadapter.controller;


import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

  @Autowired
  @Qualifier("rmConnectionFactory")
  ConnectionFactory factory;

  @RequestMapping(value = "/rabbitHealth", method = RequestMethod.GET, produces = "application/json")
  public boolean rabbitHealth(){

    RabbitAdmin rabbitAdmin = new RabbitAdmin(factory);

    String result1 = rabbitAdmin.getQueueProperties(ACTION_FIELD_QUEUE).getProperty("QUEUE_NAME");

    if(result1.equals("Action.Field")){
      return true;
    }

    return false;
  }


}

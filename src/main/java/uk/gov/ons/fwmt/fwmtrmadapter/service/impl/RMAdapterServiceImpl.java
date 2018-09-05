package uk.gov.ons.fwmt.fwmtrmadapter.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.fwmt.fwmtgatewaycommon.data.DummyTMResponse;
import uk.gov.ons.fwmt.fwmtrmadapter.data.DummyRMReturn;
import uk.gov.ons.fwmt.fwmtrmadapter.message.JobServiceProducer;
import uk.gov.ons.fwmt.fwmtrmadapter.message.RMProducer;
import uk.gov.ons.fwmt.fwmtrmadapter.service.MessageConverter;
import uk.gov.ons.fwmt.fwmtrmadapter.service.RMAdapterService;

@Slf4j
@Component
public class RMAdapterServiceImpl implements RMAdapterService {

  @Autowired
  private JobServiceProducer jobServiceProducer;

  @Autowired
  private MessageConverter messageConverter;

  @Autowired
  private RMProducer rmProducer;


  public void sendJobRequest(ActionInstruction actionInstruction) {
    if (actionInstruction.getActionRequest() != null) {
      jobServiceProducer.sendMessage(messageConverter.createJob(actionInstruction));
    }
    else if (actionInstruction.getActionUpdate() != null) {
      jobServiceProducer.sendMessage(messageConverter.updateJob(actionInstruction));
    }
    else if (actionInstruction.getActionCancel() != null) {
      jobServiceProducer.sendMessage(messageConverter.cancelJob(actionInstruction));
    }
  }

  public void returnJobRequest(DummyTMResponse response) {
    DummyRMReturn returnMsg = convertTMResponse(response);
    rmProducer.sendJobRequestResponse(returnMsg);

  }

  protected DummyRMReturn convertTMResponse(DummyTMResponse response) {
    DummyRMReturn rmReturnMessage = new DummyRMReturn();
    rmReturnMessage.setIdentity(response.getIdentity());
    return rmReturnMessage;
  }
}

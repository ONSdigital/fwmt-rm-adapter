package uk.gov.ons.fwmt.fwmtrmadapter.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.fwmt.fwmtgatewaycommon.DummyTMResponse;
import uk.gov.ons.fwmt.fwmtrmadapter.data.DummyRMReturn;
import uk.gov.ons.fwmt.fwmtrmadapter.message.RMProducer;
import uk.gov.ons.fwmt.fwmtrmadapter.message.impl.JobServiceProducerImpl;
import uk.gov.ons.fwmt.fwmtrmadapter.service.RMAdapterService;

@Slf4j
@Component
public class RMAdapterServiceImpl implements RMAdapterService {

  @Autowired JobServiceProducerImpl jobServiceProducer;
  @Autowired MessageConverterImpl messageConverter;
  @Autowired RMProducer rmProducer;


  public void sendJobRequest(ActionInstruction actionInstruction) {
    if (actionInstruction.getActionRequest().getActionType().matches("create")) {
      jobServiceProducer.sendMessage(messageConverter.createJob(actionInstruction));
    }
    if (actionInstruction.getActionRequest().getActionType().matches("update")) {
      //TODO add code for update

    }
    if (actionInstruction.getActionRequest().getActionType().matches("cancel")) {
      jobServiceProducer.sendMessage(messageConverter.cancelJob(actionInstruction));
    }
  }

  public void returnJobRequest(DummyTMResponse response) {
    DummyRMReturn returnMsg = convertTMResponse(response);
    rmProducer.sendJobRequestResponse(returnMsg);

  }

  private DummyRMReturn convertTMResponse(DummyTMResponse response) {
    DummyRMReturn rmReturnMessage = new DummyRMReturn();
    rmReturnMessage.setIdentity(response.getIdentity());
    return rmReturnMessage;
  }
}

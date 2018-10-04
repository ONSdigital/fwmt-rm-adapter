package uk.gov.ons.fwmt.fwmtrmadapter.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.fwmt.fwmtgatewaycommon.error.CTPException;
import uk.gov.ons.fwmt.fwmtohsjobstatusnotification.FwmtOHSJobStatusNotification;
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

  public void sendJobRequest(ActionInstruction actionInstruction) throws CTPException {
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

  public void returnJobRequest(FwmtOHSJobStatusNotification response) throws CTPException {
    rmProducer.sendJobRequestResponse(response);

  }

}

package uk.gov.ons.fwmt.fwmtrmadapter.service;

import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.fwmt.fwmtgatewaycommon.data.FWMTCancelJobRequest;
import uk.gov.ons.fwmt.fwmtgatewaycommon.data.FWMTCreateJobRequest;
import uk.gov.ons.fwmt.fwmtgatewaycommon.data.FWMTUpdateJobRequest;
import uk.gov.ons.fwmt.fwmtgatewaycommon.error.CTPException;

public interface MessageConverter {
  FWMTCreateJobRequest createJob(ActionInstruction actionInstruction) throws CTPException;

  FWMTCancelJobRequest cancelJob(ActionInstruction actionInstruction);

  FWMTUpdateJobRequest updateJob(ActionInstruction actionInstruction);
}

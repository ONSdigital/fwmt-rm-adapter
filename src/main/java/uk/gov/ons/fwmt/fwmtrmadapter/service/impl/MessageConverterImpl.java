package uk.gov.ons.fwmt.fwmtrmadapter.service.impl;

import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.fwmt.fwmtgatewaycommon.Address;
import uk.gov.ons.fwmt.fwmtgatewaycommon.FWMTCancelJobRequest;
import uk.gov.ons.fwmt.fwmtgatewaycommon.FWMTCreateJobRequest;
import uk.gov.ons.fwmt.fwmtrmadapter.service.MessageConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class MessageConverterImpl implements MessageConverter {

  @Override public FWMTCreateJobRequest createJob(ActionInstruction actionInstruction) {
    FWMTCreateJobRequest fwmtCreateJobRequest = new FWMTCreateJobRequest();
    ActionRequest actionRequest = actionInstruction.getActionRequest();
    ActionAddress actionAddress = actionRequest.getAddress();

    Address address = new Address();
    address.setLatitude(actionAddress.getLatitude());
    address.setLongitude(actionAddress.getLongitude());
    address.setLine1(actionAddress.getLine1());
    address.setLine2(actionAddress.getLine2());
    address.setLine3(actionAddress.getLine3());
    address.setLine4(actionAddress.getLine4());
    address.setPostCode(actionAddress.getPostcode());
    address.setTownName(actionAddress.getTownName());

    fwmtCreateJobRequest.setJobIdentity(actionRequest.getActionId());
    fwmtCreateJobRequest.setSurveyType(actionRequest.getSurveyRef());
    //TODO set as per data mapping
    //fwmtCreateJobRequest.setMandatoryResourceAuthNo(actionRequest();
    //fwmtCreateJobRequest.setPreallocatedJob();
    fwmtCreateJobRequest.setDueDate(LocalDate.parse(actionRequest.getReturnByDate(), DateTimeFormatter.BASIC_ISO_DATE));
    fwmtCreateJobRequest.setAddress(address);
    fwmtCreateJobRequest.setActionType("Create");
    //TODO add caseId additional property


    return fwmtCreateJobRequest;
  }

  @Override public FWMTCancelJobRequest cancelJob(ActionInstruction actionInstruction) {
    FWMTCancelJobRequest fwmtCancelJobRequest = new FWMTCancelJobRequest();
    fwmtCancelJobRequest.setActionType("Cancel");
    fwmtCancelJobRequest.setJobIdentity(actionInstruction.getActionCancel().getActionId());
    fwmtCancelJobRequest.setReason(actionInstruction.getActionCancel().getReason());
    return fwmtCancelJobRequest;
  }
}

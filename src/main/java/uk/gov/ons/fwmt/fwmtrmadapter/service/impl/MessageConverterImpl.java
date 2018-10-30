package uk.gov.ons.fwmt.fwmtrmadapter.service.impl;

import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.fwmt.fwmtgatewaycommon.data.Address;
import uk.gov.ons.fwmt.fwmtgatewaycommon.data.FWMTCancelJobRequest;
import uk.gov.ons.fwmt.fwmtgatewaycommon.data.FWMTCreateJobRequest;
import uk.gov.ons.fwmt.fwmtgatewaycommon.data.FWMTUpdateJobRequest;
import uk.gov.ons.fwmt.fwmtrmadapter.service.MessageConverter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class MessageConverterImpl implements MessageConverter {

  @Override
  public FWMTCreateJobRequest createJob(ActionInstruction actionInstruction) {
    FWMTCreateJobRequest fwmtCreateJobRequest = new FWMTCreateJobRequest();
    ActionRequest actionRequest = actionInstruction.getActionRequest();
    ActionAddress actionAddress = actionRequest.getAddress();

    Address address = new Address();
    address.setLine1(actionAddress.getLine1());
    address.setLine2(actionAddress.getLine2());
    address.setLine3(actionAddress.getLine3());
    address.setLine4(actionAddress.getLine4());
    address.setTownName(actionAddress.getTownName());
    address.setPostCode(actionAddress.getPostcode());
    address.setLatitude(actionAddress.getLatitude());
    address.setLongitude(actionAddress.getLongitude());
    address.setOrganisationName(actionAddress.getOrganisationName());

    fwmtCreateJobRequest.setJobIdentity(actionRequest.getCaseRef());
    fwmtCreateJobRequest.setSurveyType(actionRequest.getSurveyRef());
    //TODO set as per data mapping
    //fwmtCreateJobRequest.setMandatoryResourceAuthNo(actionRequest();
    //fwmtCreateJobRequest.setPreallocatedJob();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    fwmtCreateJobRequest.setDueDate(LocalDate.parse(actionRequest.getReturnByDate(), formatter));
    fwmtCreateJobRequest.setAddress(address);
    fwmtCreateJobRequest.setActionType("Create");

    Map<String, String> additionalPropertiesMap = new HashMap<>();
    additionalPropertiesMap.put("caseId", actionRequest.getCaseId());
    if (actionRequest.getSurveyRef().equals("CE")) {
      additionalPropertiesMap.put("EstablishmentType", actionAddress.getEstabType());
      additionalPropertiesMap.put("Category", actionAddress.getCategory());
      additionalPropertiesMap.put("LAD", actionAddress.getLadCode());
      additionalPropertiesMap.put("Region", actionRequest.getRegion());
      additionalPropertiesMap.put("CaseId", actionRequest.getCaseId());
    }
    fwmtCreateJobRequest.setAdditionalProperties(additionalPropertiesMap);

    return fwmtCreateJobRequest;
  }

  @Override
  public FWMTCancelJobRequest cancelJob(ActionInstruction actionInstruction) {
    FWMTCancelJobRequest fwmtCancelJobRequest = new FWMTCancelJobRequest();
    fwmtCancelJobRequest.setActionType("Cancel");
    fwmtCancelJobRequest.setJobIdentity(actionInstruction.getActionCancel().getActionId());
    fwmtCancelJobRequest.setReason(actionInstruction.getActionCancel().getReason());

    return fwmtCancelJobRequest;
  }

  @Override
  public FWMTUpdateJobRequest updateJob(ActionInstruction actionInstruction) {
    FWMTUpdateJobRequest fwmtUpdateJobRequest = new FWMTUpdateJobRequest();
    fwmtUpdateJobRequest.setActionType("update");

    return fwmtUpdateJobRequest;
  }

}

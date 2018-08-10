package uk.gov.ons.fwmt.fwmtrmadapter.service.impl;

import org.springframework.stereotype.Component;
import uk.gov.ons.fwmt.fwmtgatewaycommon.DummyTMResponse;
import uk.gov.ons.fwmt.fwmtrmadapter.message.JobServiceProducer;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.ons.fwmt.fwmtrmadapter.message.RMProducer;
import uk.gov.ons.fwmt.fwmtrmadapter.service.RMAdapterService;
import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.fwmt.fwmtgatewaycommon.Address;
import uk.gov.ons.fwmt.fwmtgatewaycommon.FWMTCreateJobRequest;
import uk.gov.ons.fwmt.fwmtrmadapter.data.DummyRMReturn;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class RMAdapterServiceImpl implements RMAdapterService {

  @Autowired
  JobServiceProducer jobServiceProducer;

  @Autowired
  RMProducer rmProducer;

  public void sendJobRequest(ActionInstruction actionInstruction) {
    FWMTCreateJobRequest createJobRequest = transformActionInstruction(actionInstruction);
    jobServiceProducer.sendCreateJobRequest(createJobRequest);
  }

  private FWMTCreateJobRequest transformActionInstruction(ActionInstruction actionInstruction) {
    FWMTCreateJobRequest createJobRequest = new FWMTCreateJobRequest();
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

    createJobRequest.setJobIdentity(actionRequest.getCaseId());
    createJobRequest.setSurveyType(actionRequest.getSurveyRef());
    //createJobRequest.setMandatoryResourceAuthNo(actionRequest();
    //createJobRequest.setPreallocatedJob();
    createJobRequest.setDueDate(LocalDate.parse(actionRequest.getReturnByDate(), DateTimeFormatter.BASIC_ISO_DATE));
    createJobRequest.setAddress(address);

    return createJobRequest;
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

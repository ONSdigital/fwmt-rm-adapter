package uk.gov.ons.fwmt.fwmtrmadapter.service.impl;

import org.springframework.stereotype.Component;
import uk.gov.ons.fwmt.fwmtrmadapter.message.JobServiceProducer;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.ons.fwmt.fwmtrmadapter.service.RMAdapterService;
import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.fwmt.fwmtgatewaycommon.Address;
import uk.gov.ons.fwmt.fwmtgatewaycommon.FWMTCreateJobRequest;

import java.time.LocalDate;

@Component
public class RMAdapterServiceImpl implements RMAdapterService {

  @Autowired
  JobServiceProducer jobServiceProducer;

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
    //createJobRequest.setMandatoryResourceAuthNo(actionRequest.);
    //createJobRequest.setPreallocatedJob();
    createJobRequest.setDueDate(LocalDate.parse(actionRequest.getReturnByDate()));
    createJobRequest.setAddress(address);

    return createJobRequest;
  }

}

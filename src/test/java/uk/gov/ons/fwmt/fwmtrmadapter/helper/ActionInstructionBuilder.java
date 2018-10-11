package uk.gov.ons.fwmt.fwmtrmadapter.helper;

import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;
import uk.gov.ons.ctp.response.action.message.instruction.ActionCancel;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.message.instruction.ActionUpdate;

import java.math.BigDecimal;

public class ActionInstructionBuilder {

  public ActionInstruction createActionInstructionBuilder() {
    ActionInstruction actionInstruction = new ActionInstruction();
    ActionRequest actionRequest = new ActionRequest();
    ActionAddress actionAddress = new ActionAddress();

    actionRequest.setCaseId("testCaseId");
    actionRequest.setSurveyRef("testSurveyRef");
    actionRequest.setReturnByDate("11/11/2000");

    actionAddress.setLatitude(BigDecimal.valueOf(1000.00));
    actionAddress.setLongitude(BigDecimal.valueOf(1000.00));
    actionAddress.setLine1("addressLine1");
    actionAddress.setLine2("addressLine2");
    actionAddress.setLine3("addressLine3");
    actionAddress.setLine4("addressLine4");
    actionAddress.setPostcode("testPostcode");
    actionAddress.setTownName("testTownName");

    actionRequest.setAddress(actionAddress);
    actionInstruction.setActionRequest(actionRequest);

    return actionInstruction;
  }

  public ActionInstruction cancelActionInstructionBuilder() {
    ActionInstruction actionInstruction = new ActionInstruction();
    ActionCancel actionCancel = new ActionCancel();

    actionCancel.setReason("reason");
    actionCancel.setActionId("testActionID");

    actionInstruction.setActionCancel(actionCancel);

    return actionInstruction;
  }

  public ActionInstruction updateActionInstructionBuilder() {
    ActionInstruction actionInstruction = new ActionInstruction();
    ActionUpdate actionUpdate = new ActionUpdate();

    actionInstruction.setActionUpdate(actionUpdate);

    return actionInstruction;
  }
}

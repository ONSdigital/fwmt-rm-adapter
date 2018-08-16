package uk.gov.ons.fwmt.fwmtrmadapter.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.fwmt.fwmtgatewaycommon.FWMTCancelJobRequest;
import uk.gov.ons.fwmt.fwmtgatewaycommon.FWMTCreateJobRequest;
import uk.gov.ons.fwmt.fwmtrmadapter.helper.ActionInstructionBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class MessageConverterImplTest {

  @InjectMocks MessageConverterImpl messageConverter;

  @Test
  public void createJob() {
    //Given
    ActionInstructionBuilder actionInstructionBuilder = new ActionInstructionBuilder();
    ActionInstruction actionInstruction = actionInstructionBuilder.createActionInstructionBuilder();

    //When
    FWMTCreateJobRequest result = messageConverter.createJob(actionInstruction);

    //Then
    assertEquals(actionInstruction.getActionRequest().getSurveyRef(), result.getSurveyType());
    assertEquals(
        LocalDate.parse(actionInstruction.getActionRequest().getReturnByDate(), DateTimeFormatter.BASIC_ISO_DATE),
        result.getDueDate());
    assertEquals(actionInstruction.getActionRequest().getAddress().getLatitude(),
        result.getAddress().getLatitude());
    assertEquals(actionInstruction.getActionRequest().getAddress().getLongitude(),
        result.getAddress().getLongitude());
    assertEquals(actionInstruction.getActionRequest().getAddress().getPostcode(),
        result.getAddress().getPostCode());
    assertEquals(actionInstruction.getActionRequest().getAddress().getTownName(),
        result.getAddress().getTownName());
    assertEquals(actionInstruction.getActionRequest().getAddress().getLine1(), result.getAddress().getLine1());
    assertEquals(actionInstruction.getActionRequest().getAddress().getLine2(), result.getAddress().getLine2());
    assertEquals(actionInstruction.getActionRequest().getAddress().getLine3(), result.getAddress().getLine3());
    assertEquals(actionInstruction.getActionRequest().getAddress().getLine4(), result.getAddress().getLine4());
  }

  @Test
  public void cancelJob() {
    //Given
    ActionInstructionBuilder actionInstructionBuilder = new ActionInstructionBuilder();
    ActionInstruction actionInstruction = actionInstructionBuilder.cancelActionInstructionBuilder();

    //When
    FWMTCancelJobRequest result = messageConverter.cancelJob(actionInstruction);

    //Then
    assertEquals(actionInstruction.getActionCancel().getActionId(), result.getJobIdentity());
    assertEquals(actionInstruction.getActionCancel().getReason(), result.getReason());

  }
}
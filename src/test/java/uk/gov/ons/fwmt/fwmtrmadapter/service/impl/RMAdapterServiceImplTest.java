package uk.gov.ons.fwmt.fwmtrmadapter.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.fwmt.fwmtgatewaycommon.DummyTMResponse;
import uk.gov.ons.fwmt.fwmtgatewaycommon.FWMTCreateJobRequest;
import uk.gov.ons.fwmt.fwmtrmadapter.data.DummyRMReturn;
import uk.gov.ons.fwmt.fwmtrmadapter.helper.ActionInstructionBuilder;
import uk.gov.ons.fwmt.fwmtrmadapter.message.JobServiceProducer;
import uk.gov.ons.fwmt.fwmtrmadapter.message.impl.RMProducerImpl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RMAdapterServiceImplTest {

  @InjectMocks RMAdapterServiceImpl rmAdapterService;
  @Mock JobServiceProducer jobServiceProducer;
  @Mock RMProducerImpl rmProducer;
  @Captor ArgumentCaptor argCaptor;

  @Test
  public void sendJobRequest() {
    //Given
    ActionInstructionBuilder actionInstructionBuilder = new ActionInstructionBuilder();
    ActionInstruction testActionInstruction = actionInstructionBuilder.createActionInstructionBuilder();

    //When
    rmAdapterService.sendJobRequest(testActionInstruction);

    //Then
    verify(jobServiceProducer).sendCreateJobRequest((FWMTCreateJobRequest) argCaptor.capture());
    FWMTCreateJobRequest result = (FWMTCreateJobRequest) argCaptor.getValue();
    assertEquals(testActionInstruction.getActionRequest().getCaseId(), result.getJobIdentity());
    assertEquals(testActionInstruction.getActionRequest().getSurveyRef(), result.getSurveyType());
    assertEquals(
        LocalDate.parse(testActionInstruction.getActionRequest().getReturnByDate(), DateTimeFormatter.BASIC_ISO_DATE),
        result.getDueDate());
    assertEquals(testActionInstruction.getActionRequest().getAddress().getLatitude(),
        result.getAddress().getLatitude());
    assertEquals(testActionInstruction.getActionRequest().getAddress().getLongitude(),
        result.getAddress().getLongitude());
    assertEquals(testActionInstruction.getActionRequest().getAddress().getPostcode(),
        result.getAddress().getPostCode());
    assertEquals(testActionInstruction.getActionRequest().getAddress().getTownName(),
        result.getAddress().getTownName());
    assertEquals(testActionInstruction.getActionRequest().getAddress().getLine1(), result.getAddress().getLine1());
    assertEquals(testActionInstruction.getActionRequest().getAddress().getLine2(), result.getAddress().getLine2());
    assertEquals(testActionInstruction.getActionRequest().getAddress().getLine3(), result.getAddress().getLine3());
    assertEquals(testActionInstruction.getActionRequest().getAddress().getLine4(), result.getAddress().getLine4());
  }

  @Test
  public void transformActionInstruction() {
    //Given
    ActionInstructionBuilder actionInstructionBuilder = new ActionInstructionBuilder();
    ActionInstruction actionInstruction = actionInstructionBuilder.createActionInstructionBuilder();

    //When
    FWMTCreateJobRequest result = rmAdapterService.transformActionInstruction(actionInstruction);

    //Then
    assertEquals(actionInstruction.getActionRequest().getCaseId(), result.getJobIdentity());
    assertEquals(actionInstruction.getActionRequest().getSurveyRef(), result.getSurveyType());
    assertEquals(
        LocalDate.parse(actionInstruction.getActionRequest().getReturnByDate(), DateTimeFormatter.BASIC_ISO_DATE),
        result.getDueDate());
    assertEquals(actionInstruction.getActionRequest().getAddress().getLatitude(), result.getAddress().getLatitude());
    assertEquals(actionInstruction.getActionRequest().getAddress().getLongitude(), result.getAddress().getLongitude());
    assertEquals(actionInstruction.getActionRequest().getAddress().getPostcode(), result.getAddress().getPostCode());
    assertEquals(actionInstruction.getActionRequest().getAddress().getTownName(), result.getAddress().getTownName());
    assertEquals(actionInstruction.getActionRequest().getAddress().getLine1(), result.getAddress().getLine1());
    assertEquals(actionInstruction.getActionRequest().getAddress().getLine2(), result.getAddress().getLine2());
    assertEquals(actionInstruction.getActionRequest().getAddress().getLine3(), result.getAddress().getLine3());
    assertEquals(actionInstruction.getActionRequest().getAddress().getLine4(), result.getAddress().getLine4());
  }

  @Test
  public void returnJobRequest() {
    //Given
    DummyTMResponse response = new DummyTMResponse();
    response.setIdentity("dummy");

    //When
    rmAdapterService.returnJobRequest(response);

    //Then
    Mockito.verify(rmProducer).sendJobRequestResponse((DummyRMReturn) argCaptor.capture());
    DummyRMReturn result = (DummyRMReturn) argCaptor.getValue();
    assertEquals(response.getIdentity(), result.getIdentity());
  }

  @Test
  public void convertTMResponse() {
    //Given
    DummyTMResponse response = new DummyTMResponse();
    response.setIdentity("dummy");

    //When
    DummyRMReturn rmReturn = rmAdapterService.convertTMResponse(response);

    //Then
    assertEquals(response.getIdentity(), rmReturn.getIdentity());

  }
}
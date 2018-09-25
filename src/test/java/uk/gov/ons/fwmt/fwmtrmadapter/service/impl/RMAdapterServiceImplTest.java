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
import uk.gov.ons.fwmt.fwmtgatewaycommon.data.DummyTMResponse;
import uk.gov.ons.fwmt.fwmtgatewaycommon.data.FWMTCancelJobRequest;
import uk.gov.ons.fwmt.fwmtgatewaycommon.data.FWMTCreateJobRequest;
import uk.gov.ons.fwmt.fwmtgatewaycommon.data.FWMTUpdateJobRequest;
import uk.gov.ons.fwmt.fwmtrmadapter.common.error.CTPException;
import uk.gov.ons.fwmt.fwmtrmadapter.data.DummyRMReturn;
import uk.gov.ons.fwmt.fwmtrmadapter.helper.ActionInstructionBuilder;
import uk.gov.ons.fwmt.fwmtrmadapter.message.impl.JobServiceProducerImpl;
import uk.gov.ons.fwmt.fwmtrmadapter.message.impl.RMProducerImpl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RMAdapterServiceImplTest {

  @InjectMocks
  private RMAdapterServiceImpl rmAdapterService;

  @Mock
  private JobServiceProducerImpl jobServiceProducer;

  @Mock
  private MessageConverterImpl messageConverter;

  @Mock
  private RMProducerImpl rmProducer;

  @Captor
  private ArgumentCaptor argCaptor;

  @Test
  public void sendCreateJobRequest() throws CTPException {
    //Given
    ActionInstructionBuilder actionInstructionBuilder = new ActionInstructionBuilder();
    ActionInstruction actionInstruction = actionInstructionBuilder.createActionInstructionBuilder();
    FWMTCreateJobRequest fwmtCreateJobRequest = new FWMTCreateJobRequest();
    when(messageConverter.createJob(actionInstruction)).thenReturn(fwmtCreateJobRequest);

    //When
    rmAdapterService.sendJobRequest(actionInstruction);

    //Then
    verify(jobServiceProducer).sendMessage(fwmtCreateJobRequest);
  }

  //  @Ignore("Code to update needs to be written")
  @Test
  public void sendUpdateJobRequest() throws CTPException {
    //Given
    ActionInstructionBuilder actionInstructionBuilder = new ActionInstructionBuilder();
    ActionInstruction actionInstruction = actionInstructionBuilder.updateActionInstructionBuilder();
    FWMTUpdateJobRequest fwmtUpdateJobRequest = new FWMTUpdateJobRequest();
    when(messageConverter.updateJob(actionInstruction)).thenReturn(fwmtUpdateJobRequest);

    //When
    rmAdapterService.sendJobRequest(actionInstruction);

    //Then
    verify(jobServiceProducer).sendMessage(fwmtUpdateJobRequest);
  }

  @Test
  public void sendCancelJobRequest() throws CTPException {
    //Given
    ActionInstructionBuilder actionInstructionBuilder = new ActionInstructionBuilder();
    ActionInstruction actionInstruction = actionInstructionBuilder.cancelActionInstructionBuilder();
    FWMTCancelJobRequest fwmtCancelJobRequest = new FWMTCancelJobRequest();
    when(messageConverter.cancelJob(actionInstruction)).thenReturn(fwmtCancelJobRequest);

    //When
    rmAdapterService.sendJobRequest(actionInstruction);

    //Then
    verify(jobServiceProducer).sendMessage(fwmtCancelJobRequest);
  }

  @Test
  public void returnJobRequest() throws CTPException {
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
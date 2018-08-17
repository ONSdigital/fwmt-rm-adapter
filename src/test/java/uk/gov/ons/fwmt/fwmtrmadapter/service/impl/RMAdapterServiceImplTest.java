package uk.gov.ons.fwmt.fwmtrmadapter.service.impl;

import org.junit.Ignore;
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
import uk.gov.ons.fwmt.fwmtrmadapter.data.DummyRMReturn;
import uk.gov.ons.fwmt.fwmtrmadapter.helper.ActionInstructionBuilder;
import uk.gov.ons.fwmt.fwmtrmadapter.message.impl.RMProducerImpl;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class RMAdapterServiceImplTest {

  @InjectMocks
  private RMAdapterServiceImpl rmAdapterService;

  @Mock
  private MessageConverterImpl messageConverter;

  @Mock
  private RMProducerImpl rmProducer;

  @Captor
  private ArgumentCaptor argCaptor;

  @Ignore
  @Test
  public void sendCreateJobRequest() {
    //Given
    ActionInstructionBuilder actionInstructionBuilder = new ActionInstructionBuilder();
    ActionInstruction actionInstruction = actionInstructionBuilder.createActionInstructionBuilder();

    //When
    rmAdapterService.sendJobRequest(actionInstruction);
  }

  @Ignore
  @Test
  public void sendUpdateJobRequest() {

  }

  @Ignore
  @Test
  public void sendCancelJobRequest() {
    //Given
    ActionInstructionBuilder actionInstructionBuilder = new ActionInstructionBuilder();
    ActionInstruction actionInstruction = actionInstructionBuilder.cancelActionInstructionBuilder();

    //When
    rmAdapterService.sendJobRequest(actionInstruction);
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
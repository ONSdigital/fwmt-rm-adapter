package uk.gov.ons.fwmt.fwmtrmadapter.message.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;
import uk.gov.ons.ctp.response.action.message.instruction.ActionCancel;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.fwmt.fwmtgatewaycommon.error.CTPException;
import uk.gov.ons.fwmt.fwmtrmadapter.service.impl.RMAdapterServiceImpl;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RMReceiverImplTest {

  @InjectMocks
  private RMReceiverImpl rmReceiver;

  @Mock
  private RMAdapterServiceImpl rmAdapterService;

  private final String ACTION_REQUEST_XML = "<ins:actionInstruction xmlns:ins=\"http://ons.gov.uk/ctp/response/action/message/instruction\"><actionRequest><actionId>actionId</actionId><responseRequired>false</responseRequired><actionPlan>string</actionPlan><actionType>actionType</actionType><questionSet>string</questionSet><contact><title>string</title><forename>string</forename><surname>string</surname><phoneNumber>string</phoneNumber><emailAddress>string</emailAddress></contact><address><sampleUnitRef>string</sampleUnitRef><type>string</type><estabType>string</estabType><locality>string</locality><organisationName>string</organisationName><category>string</category><line1>line1</line1><line2>line2</line2><line3>line3</line3><line4>line4</line4><townName>Town</townName><postcode>P05T C0D3</postcode><ladCode>string</ladCode><latitude>1234.56</latitude><longitude>2345.67</longitude></address><caseId>caseId</caseId><returnByDate>19950718</returnByDate><priority>lower</priority><caseRef>string</caseRef><iac>string</iac><surveyRef>surveyRef</surveyRef><events><event>string</event></events><exerciseRef>string</exerciseRef></actionRequest></ins:actionInstruction>\n";

  private final String ACTION_CANCEL_XML = "<ins:actionInstruction xmlns:ins=\"http://ons.gov.uk/ctp/response/action/message/instruction\"><actionCancel><actionId>actionId</actionId><responseRequired>true</responseRequired><reason>Reason</reason></actionCancel></ins:actionInstruction>";

  @Test
  public void receiveMessageCreate() throws CTPException {

    rmReceiver.receiveMessage(ACTION_REQUEST_XML);

    ArgumentCaptor <ActionInstruction> actionInstructionArgumentCaptor = ArgumentCaptor.forClass(ActionInstruction.class);

    verify(rmAdapterService).sendJobRequest(actionInstructionArgumentCaptor.capture());

    ActionInstruction actionInstruction = actionInstructionArgumentCaptor.getValue();

    assertNotNull(actionInstruction.getActionRequest());
    assertNull(actionInstruction.getActionCancel());
    assertNull(actionInstruction.getActionUpdate());

    ActionRequest actionRequest = actionInstruction.getActionRequest();

    assertEquals(actionRequest.getCaseId(),"caseId");
    assertEquals(actionRequest.getActionType(),"actionType");
    assertEquals(actionRequest.getActionId(),"actionId");
    assertEquals(actionRequest.getReturnByDate(),"19950718");
    assertEquals(actionRequest.getSurveyRef(),"surveyRef");

    ActionAddress address = actionRequest.getAddress();

    assertEquals(address.getLatitude(), BigDecimal.valueOf(1234.56));
    assertEquals(address.getLongitude(), BigDecimal.valueOf(2345.67));
    assertEquals(address.getLine1(),"line1");
    assertEquals(address.getLine2(),"line2");
    assertEquals(address.getLine3(),"line3");
    assertEquals(address.getLine4(),"line4");
    assertEquals(address.getPostcode(),"P05T C0D3");
    assertEquals(address.getTownName(),"Town");

    verify(rmAdapterService).sendJobRequest(actionInstruction);

  }

  @Test
  public void receiveMessageCancel() throws CTPException {

    rmReceiver.receiveMessage(ACTION_CANCEL_XML);

    ArgumentCaptor <ActionInstruction> actionInstructionArgumentCaptor = ArgumentCaptor.forClass(ActionInstruction.class);

    verify(rmAdapterService).sendJobRequest(actionInstructionArgumentCaptor.capture());

    ActionInstruction actionInstruction = actionInstructionArgumentCaptor.getValue();

    assertNotNull(actionInstruction.getActionCancel());
    assertNull(actionInstruction.getActionRequest());
    assertNull(actionInstruction.getActionUpdate());

    ActionCancel actionCancel = actionInstruction.getActionCancel();

    assertEquals(actionCancel.getReason(),"Reason");
    assertEquals(actionCancel.getActionId(),"actionId");

    verify(rmAdapterService).sendJobRequest(actionInstruction);

  }
}

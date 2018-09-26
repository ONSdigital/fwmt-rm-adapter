package uk.gov.ons.fwmt.fwmtrmadapter.service;

import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.fwmt.fwmtgatewaycommon.data.DummyTMResponse;
import uk.gov.ons.fwmt.fwmtgatewaycommon.error.CTPException;

import javax.xml.bind.JAXBException;

public interface RMAdapterService {

  void sendJobRequest(ActionInstruction actionInstruction) throws JAXBException, CTPException;

  void returnJobRequest(DummyTMResponse response) throws CTPException;

}

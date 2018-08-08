package uk.gov.ons.fwmt.fwmtrmadapter.service;

import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;

import javax.xml.bind.JAXBException;

public interface RMAdapterService {

  void sendJobRequest(ActionInstruction actionInstruction) throws JAXBException;

}

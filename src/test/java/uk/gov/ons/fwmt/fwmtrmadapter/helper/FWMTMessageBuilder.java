package uk.gov.ons.fwmt.fwmtrmadapter.helper;

import uk.gov.ons.fwmt.fwmtgatewaycommon.Address;
import uk.gov.ons.fwmt.fwmtgatewaycommon.FWMTCreateJobRequest;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FWMTMessageBuilder {

  public FWMTCreateJobRequest buildFWMTCreateJobRequest() {
    FWMTCreateJobRequest fwmtCreateJobRequest = new FWMTCreateJobRequest();

    Address address = new Address();
    address.setLatitude(BigDecimal.valueOf(1000.00));
    address.setLongitude(BigDecimal.valueOf(1000.00));
    address.setLine1("testLine1");
    address.setLine2("testLine2");
    address.setLine3("testLine3");
    address.setLine4("testLine4");
    address.setPostCode("testPostCode");
    address.setTownName("testTownName");
    fwmtCreateJobRequest.setActionType("create");
    fwmtCreateJobRequest.setAddress(address);
    fwmtCreateJobRequest.setDueDate(LocalDate.of(2000, 11, 11));
    fwmtCreateJobRequest.setJobIdentity("testJobIdentity");
    fwmtCreateJobRequest.setMandatoryResourceAuthNo("testMandatoryResourceAuthNo");
    fwmtCreateJobRequest.setPreallocatedJob(false);
    fwmtCreateJobRequest.setSurveyType("testSurveyType");

    return fwmtCreateJobRequest;
  }
}

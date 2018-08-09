package uk.gov.ons.fwmt.fwmtrmadapter.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Data
@AllArgsConstructor
@NoArgsConstructor

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "DummyRMReturn"
)
@XmlRootElement(name = "DummyRMReturn")
public class DummyRMReturn {
  private String identity;
}

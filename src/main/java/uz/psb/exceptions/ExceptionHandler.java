package uz.psb.exceptions;

import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;
import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;

import javax.wsdl.Fault;
import javax.xml.namespace.QName;

/**
 * dastur ichida qandaydir istisno xolatlari bo'lsa shunda qaytadigan soap fault
 */
public class ExceptionHandler extends SoapFaultMappingExceptionResolver {

    @Override
    protected void customizeFault(Object endpoint, Exception ex, SoapFault fault) {
        logger.warn("Exception processed ", ex);
        if (ex instanceof Exception) {
            Exception status = ex;
            SoapFaultDetail detail = fault.addFaultDetail();
            detail.addFaultDetailElement(QName.valueOf("statusCode")).addText("400");
            detail.addFaultDetailElement(QName.valueOf("message")).addText(status.getMessage());
        }
    }

}

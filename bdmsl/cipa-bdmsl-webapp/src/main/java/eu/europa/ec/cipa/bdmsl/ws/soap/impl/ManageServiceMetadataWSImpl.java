package eu.europa.ec.cipa.bdmsl.ws.soap.impl;

import eu.europa.ec.cipa.bdmsl.common.bo.ServiceMetadataPublisherBO;
import eu.europa.ec.cipa.bdmsl.common.exception.BadRequestException;
import eu.europa.ec.cipa.bdmsl.service.IManageServiceMetadataService;
import eu.europa.ec.cipa.bdmsl.util.LogEvents;
import eu.europa.ec.cipa.bdmsl.ws.soap.*;
import eu.europa.ec.cipa.common.exception.Severity;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * Created by feriaad on 12/06/2015.
 */
@Service
@WebService(targetNamespace = "http://busdox.org/serviceMetadata/ManageServiceMetadataService/1.0/", name = "ManageServiceMetadataServiceSoap", endpointInterface = "eu.europa.ec.cipa.bdmsl.ws.soap.IManageServiceMetadataWS")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public class ManageServiceMetadataWSImpl extends AbstractWSImpl implements IManageServiceMetadataWS {

    @Autowired
    private IManageServiceMetadataService manageServiceMetadataService;

    @Autowired
    private MapperFactory mapperFactory;

    @Override
    @WebResult(name = "ServiceMetadataPublisherService", targetNamespace = "http://busdox.org/serviceMetadata/locator/1.0/", partName = "messagePart")
    @WebMethod(operationName = "Read", action = "http://busdox.org/serviceMetadata/ManageServiceMetadataService/1.0/:readIn")
    public ServiceMetadataPublisherServiceType read(
            @WebParam(partName = "smp", name = "ReadServiceMetadataPublisherService", targetNamespace = "http://busdox.org/serviceMetadata/locator/1.0/")
            ServiceMetadataPublisherServiceType smp
    ) throws InternalErrorFault, BadRequestFault, UnauthorizedFault, NotFoundFault {
        loggingService.info("Calling ManageServiceMetadataWSImpl.read with smp=" + ToStringBuilder.reflectionToString(smp));
        ServiceMetadataPublisherServiceType result = null;
        try {
            if (smp == null) {
                throw new BadRequestException("The input values must not be null");
            }
            String serviceMetadataPublisherID = smp.getServiceMetadataPublisherID();
            ServiceMetadataPublisherBO resultBO = manageServiceMetadataService.read(serviceMetadataPublisherID);
            result = mapperFactory.getMapperFacade().map(resultBO, ServiceMetadataPublisherServiceType.class);
            loggingService.businessLog(LogEvents.BUS_SMP_READ, resultBO.getSmpId());
        } catch (Exception exc) {
            loggingService.businessLog(Severity.ERROR, LogEvents.BUS_SMP_READ_FAILED, ToStringBuilder.reflectionToString(smp));
            loggingService.error(exc.getMessage(), exc);
            // convert the exception to the associated SOAP fault
            handleException(exc);
        }
        return result;
    }

    @Override
    @WebMethod(operationName = "Create", action = "http://busdox.org/serviceMetadata/ManageServiceMetadataService/1.0/:createIn")
    public void create(
            @WebParam(partName = "smp", name = "CreateServiceMetadataPublisherService", targetNamespace = "http://busdox.org/serviceMetadata/locator/1.0/")
            ServiceMetadataPublisherServiceType smp
    ) throws InternalErrorFault, BadRequestFault, UnauthorizedFault {
        loggingService.info("Calling ManageServiceMetadataWSImpl.create with smp=" + ToStringBuilder.reflectionToString(smp));
        try {
            if (smp == null) {
                throw new BadRequestException("The input values must not be null");
            }
            ServiceMetadataPublisherBO smpBo = mapperFactory.getMapperFacade().map(smp, ServiceMetadataPublisherBO.class);
            manageServiceMetadataService.create(smpBo);
            loggingService.businessLog(LogEvents.BUS_SMP_CREATED, smpBo.getSmpId());
        } catch (Exception exc) {
            // convert the exception to the associated SOAP fault
            try {
                loggingService.businessLog(Severity.ERROR, LogEvents.BUS_SMP_CREATION_FAILED, ToStringBuilder.reflectionToString(smp));
                loggingService.error(exc.getMessage(), exc);
                handleException(exc);
            } catch (NotFoundFault notFoundFault) {
                // never happens
                throw new InternalErrorFault(RequestContextHolder.currentRequestAttributes().getSessionId(), notFoundFault.getFaultInfo(), notFoundFault);
            }
        }
    }

    @Override
    @WebMethod(operationName = "Delete", action = "http://busdox.org/serviceMetadata/ManageServiceMetadataService/1.0/:deleteIn")
    public void delete(
            @WebParam(partName = "smp", name = "ServiceMetadataPublisherID", targetNamespace = "http://busdox.org/serviceMetadata/locator/1.0/")
            String smp
    ) throws InternalErrorFault, BadRequestFault, UnauthorizedFault, NotFoundFault {
        loggingService.info("Calling ManageServiceMetadataWSImpl.delete with smp=" + smp);
        try {
            manageServiceMetadataService.delete(smp);
            loggingService.businessLog(LogEvents.BUS_SMP_DELETED, smp);
        } catch (Exception exc) {
            // convert the exception to the associated SOAP fault
            loggingService.businessLog(Severity.ERROR, LogEvents.BUS_SMP_DELETION_FAILED, ToStringBuilder.reflectionToString(smp));
            loggingService.error(exc.getMessage(), exc);
            handleException(exc);
        }
    }

    @Override
    @WebMethod(operationName = "Update", action = "http://busdox.org/serviceMetadata/ManageServiceMetadataService/1.0/:updateIn")
    public void update(
            @WebParam(partName = "smp", name = "UpdateServiceMetadataPublisherService", targetNamespace = "http://busdox.org/serviceMetadata/locator/1.0/")
            ServiceMetadataPublisherServiceType smp
    ) throws InternalErrorFault, BadRequestFault, UnauthorizedFault, NotFoundFault {
        loggingService.info("Calling ManageServiceMetadataWSImpl.update with smp=" + ToStringBuilder.reflectionToString(smp));
        try {
            if (smp == null) {
                throw new BadRequestException("The input values must not be null");
            }
            ServiceMetadataPublisherBO smpBO = mapperFactory.getMapperFacade().map(smp, ServiceMetadataPublisherBO.class);
            manageServiceMetadataService.update(smpBO);
            loggingService.businessLog(LogEvents.BUS_SMP_UPDATED, smpBO.getSmpId());
        } catch (Exception exc) {
            // convert the exception to the associated SOAP fault
            loggingService.businessLog(Severity.ERROR, LogEvents.BUS_SMP_UPDATE_FAILED, ToStringBuilder.reflectionToString(smp));
            loggingService.error(exc.getMessage(), exc);
            handleException(exc);
        }
    }

}


package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.bdmsl.ws.soap.*;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.ws.http.HTTPException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;


@Component
public class SmlIntegrationConfiguration {

    protected final ParticipantIdentifierType PARTICIPANT_ID = new ParticipantIdentifierType("sample:value", "sample:scheme");
    protected DBDomain defaultDomain;


    protected List<IManageServiceMetadataWS> smpManagerClientMocks = new ArrayList<>();
    protected Map<IManageServiceMetadataWS, AuthenticationTestDataHolder> smpManagerClientMocksData = new HashMap<>();

    protected List<IManageParticipantIdentifierWS> smlClientMocks = new ArrayList<>();
    protected Map<IManageParticipantIdentifierWS, AuthenticationTestDataHolder> smlClientMocksData = new HashMap<>();
    protected int throwExceptionAfterParticipantCallCount = -1;

    protected Throwable throwException;

    public void reset() {
        smpManagerClientMocks.clear();
        smpManagerClientMocksData.clear();
        smlClientMocks.clear();
        smlClientMocksData.clear();


        defaultDomain = new DBDomain();
        defaultDomain.setDomainCode("default_domain_id");
        defaultDomain.setSmlSmpId("SAMPLE-SMP-ID");
        defaultDomain.setSmlRegistered(false);
        defaultDomain.setSmlBlueCoatAuth(false);
        defaultDomain.setSmlClientKeyAlias("clientAlias");
        defaultDomain.setSmlClientCertHeader("blueCoatClientHeader");
        setThrowExceptionAfterParticipantCallCount(-1);
        setThrowException(null);
    }

    @Bean
    @Scope(SCOPE_PROTOTYPE)
    public IManageServiceMetadataWS smpManagerClient(String clientKeyAlias, String clientCertHttpHeader, boolean authBlueCoat) throws BadRequestFault, UnauthorizedFault, InternalErrorFault, NotFoundFault {



        IManageServiceMetadataWS clientMock = Mockito.mock(IManageServiceMetadataWS.class);
        if (throwException!= null) {
            willThrow(throwException).given(clientMock).create(any());
            willThrow(throwException).given(clientMock).delete(any());
            willThrow(throwException).given(clientMock).read(any());
            willThrow(throwException).given(clientMock).update(any());
        }

        AuthenticationTestDataHolder dh = new AuthenticationTestDataHolder();
        dh.setAlias(clientKeyAlias);
        dh.setBlueCoatHeader(clientCertHttpHeader);
        smpManagerClientMocks.add(clientMock);
        smpManagerClientMocksData.put(clientMock, dh);
        return clientMock;
    }

    @Bean
    @Scope(SCOPE_PROTOTYPE)
    public IManageParticipantIdentifierWS smpParticipantClient(String clientKeyAlias, String clientCertHttpHeader,boolean authBlueCoat) throws UnauthorizedFault, NotFoundFault, InternalErrorFault, BadRequestFault {


        if (throwExceptionAfterParticipantCallCount >0 &&  throwExceptionAfterParticipantCallCount  <= smlClientMocks.size()){
            throw new HTTPException(400);
        }
        IManageParticipantIdentifierWS clientMock = Mockito.mock(IManageParticipantIdentifierWS.class);
        if (throwException!= null) {
            willThrow(throwException).given(clientMock).create(any());
            willThrow(throwException).given(clientMock).delete(any());
            willThrow(throwException).given(clientMock).list(any());
            willThrow(throwException).given(clientMock).createList(any());
            willThrow(throwException).given(clientMock).deleteList(any());
            willThrow(throwException).given(clientMock).migrate(any());
            willThrow(throwException).given(clientMock).prepareToMigrate(any());
        }


        AuthenticationTestDataHolder dh = new AuthenticationTestDataHolder();
        dh.setAlias(clientKeyAlias);
        dh.setBlueCoatHeader(clientCertHttpHeader);
        smlClientMocks.add(clientMock);
        smlClientMocksData.put(clientMock, dh);
        return clientMock;
    }

    public ParticipantIdentifierType getParticipantId() {
        return PARTICIPANT_ID;
    }

    public DBDomain getDefaultDomain() {
        return defaultDomain;
    }

    public List<IManageServiceMetadataWS> getSmpManagerClientMocks() {
        return smpManagerClientMocks;
    }

    public Map<IManageServiceMetadataWS, AuthenticationTestDataHolder> getSmpManagerClientMocksData() {
        return smpManagerClientMocksData;
    }

    public List<IManageParticipantIdentifierWS> getParticipantManagmentClientMocks() {
        return smlClientMocks;
    }

    public Map<IManageParticipantIdentifierWS, AuthenticationTestDataHolder> getParticipantManagmentClientMocksData() {
        return smlClientMocksData;
    }

    public int getThrowExceptionAfterParticipantCallCount() {
        return throwExceptionAfterParticipantCallCount;
    }

    public void setThrowExceptionAfterParticipantCallCount(int throwExceptionAfterParticipantCallCount) {
        this.throwExceptionAfterParticipantCallCount = throwExceptionAfterParticipantCallCount;
    }

    public Throwable getThrowException() {
        return throwException;
    }

    public void setThrowException(Throwable throwException) {
        this.throwException = throwException;
    }
}

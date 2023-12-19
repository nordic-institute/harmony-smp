/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.bdmsl.ws.soap.*;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.ws.http.HTTPException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;


@Component
public class SmlIntegrationConfiguration {

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
        defaultDomain.setSmlClientCertAuth(false);
        defaultDomain.setSmlClientKeyAlias("clientAlias");
        setThrowExceptionAfterParticipantCallCount(-1);
        setThrowException(null);
    }

    @Bean("MockIManageServiceMetadataWS")
    @Primary
    @Scope(SCOPE_PROTOTYPE)
    public IManageServiceMetadataWS smpManagerClient() throws BadRequestFault, UnauthorizedFault, InternalErrorFault, NotFoundFault {



        IManageServiceMetadataWS clientMock = Mockito.mock(IManageServiceMetadataWS.class);
        if (throwException!= null) {
            willThrow(throwException).given(clientMock).create(any());
            willThrow(throwException).given(clientMock).delete(any());
            willThrow(throwException).given(clientMock).read(any());
            willThrow(throwException).given(clientMock).update(any());
        }

        AuthenticationTestDataHolder dh = new AuthenticationTestDataHolder();
        smpManagerClientMocks.add(clientMock);
        smpManagerClientMocksData.put(clientMock, dh);
        return clientMock;
    }

    @Bean("MockIManageParticipantIdentifierWS")
    @Scope(SCOPE_PROTOTYPE)
    @Primary
    public IManageParticipantIdentifierWS smpParticipantClient() throws UnauthorizedFault, NotFoundFault, InternalErrorFault, BadRequestFault {


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
        smlClientMocks.add(clientMock);
        smlClientMocksData.put(clientMock, dh);
        return clientMock;
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

/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.peppol.smpserver.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.w3c.dom.Document;

import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.xml.serialize.write.EXMLIncorrectCharacterHandling;
import com.helger.commons.xml.serialize.write.EXMLSerializeIndent;
import com.helger.commons.xml.serialize.write.IXMLWriterSettings;
import com.helger.commons.xml.serialize.write.XMLWriter;
import com.helger.commons.xml.serialize.write.XMLWriterSettings;
import com.helger.peppol.smp.ServiceMetadataType;
import com.helger.peppol.smp.SignedServiceMetadataType;
import com.helger.peppol.smpserver.restapi.SMPServerAPI;
import com.helger.peppol.smpserver.security.SMPKeyManager;
import com.helger.photon.core.app.CApplication;
import com.helger.web.mock.MockHttpServletResponse;
import com.helger.web.scope.mgr.WebScopeManager;

/**
 * This class implements the REST interface for getting SignedServiceMetadata's.
 * PUT and DELETE are also implemented.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Path ("/{ServiceGroupId}/services/{DocumentTypeId}")
public final class ServiceMetadataInterface
{
  @Context
  private HttpServletRequest m_aHttpRequest;

  @Context
  private HttpHeaders m_aHttpHeaders;

  @Context
  private UriInfo m_aUriInfo;

  @GET
  @Produces (MediaType.TEXT_XML)
  public byte [] getServiceRegistration (@PathParam ("ServiceGroupId") final String sServiceGroupID,
                                         @PathParam ("DocumentTypeId") final String sDocumentTypeID) throws Throwable
  {
    WebScopeManager.onRequestBegin (CApplication.APP_ID_PUBLIC, m_aHttpRequest, new MockHttpServletResponse ());
    try
    {
      final SignedServiceMetadataType ret = new SMPServerAPI (new SMPServerAPIDataProvider (m_aUriInfo)).getServiceRegistration (sServiceGroupID,
                                                                                                                                 sDocumentTypeID);
      // Convert to DOM document
      final MarshallerSignedServiceMetadataType aMarshaller = new MarshallerSignedServiceMetadataType ();
      final Document aDoc = aMarshaller.write (ret);

      // Sign the document
      try
      {
        SMPKeyManager.getInstance ().signXML (aDoc.getDocumentElement ());
      }
      catch (final Exception ex)
      {
        throw new RuntimeException ("Error in signing xml", ex);
      }

      // IMPORTANT: no indent and no align!
      final IXMLWriterSettings aSettings = new XMLWriterSettings ().setIncorrectCharacterHandling (EXMLIncorrectCharacterHandling.THROW_EXCEPTION)
                                                                   .setIndent (EXMLSerializeIndent.NONE);

      // Write the result to a byte array
      final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
      if (XMLWriter.writeToStream (aDoc, aBAOS, aSettings).isFailure ())
        throw new RuntimeException ("Failed to serialize signed node!");

      return aBAOS.toByteArray ();
    }
    finally
    {
      WebScopeManager.onRequestEnd ();
    }
  }

  @PUT
  public Response saveServiceRegistration (@PathParam ("ServiceGroupId") final String sServiceGroupID,
                                           @PathParam ("DocumentTypeId") final String sDocumentTypeID,
                                           final ServiceMetadataType aServiceMetadata) throws Throwable
  {
    WebScopeManager.onRequestBegin (CApplication.APP_ID_PUBLIC, m_aHttpRequest, new MockHttpServletResponse ());
    try
    {
      if (new SMPServerAPI (new SMPServerAPIDataProvider (m_aUriInfo)).saveServiceRegistration (sServiceGroupID,
                                                                                                sDocumentTypeID,
                                                                                                aServiceMetadata,
                                                                                                RestRequestHelper.getAuth (m_aHttpHeaders))
                                                                      .isFailure ())
        return Response.status (Status.BAD_REQUEST).build ();
      return Response.ok ().build ();
    }
    finally
    {
      WebScopeManager.onRequestEnd ();
    }
  }

  @DELETE
  public Response deleteServiceRegistration (@PathParam ("ServiceGroupId") final String sServiceGroupID,
                                             @PathParam ("DocumentTypeId") final String sDocumentTypeID) throws Throwable
  {
    WebScopeManager.onRequestBegin (CApplication.APP_ID_PUBLIC, m_aHttpRequest, new MockHttpServletResponse ());
    try
    {
      if (new SMPServerAPI (new SMPServerAPIDataProvider (m_aUriInfo)).deleteServiceRegistration (sServiceGroupID,
                                                                                                  sDocumentTypeID,
                                                                                                  RestRequestHelper.getAuth (m_aHttpHeaders))
                                                                      .isFailure ())
        return Response.status (Status.BAD_REQUEST).build ();
      return Response.ok ().build ();
    }
    finally
    {
      WebScopeManager.onRequestEnd ();
    }
  }
}
<%--

    Version: MPL 1.1/EUPL 1.1

    The contents of this file are subject to the Mozilla Public License Version
    1.1 (the "License"); you may not use this file except in compliance with
    the License. You may obtain a copy of the License at:
    http://www.mozilla.org/MPL/

    Software distributed under the License is distributed on an "AS IS" basis,
    WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
    for the specific language governing rights and limitations under the
    License.

    The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)

    Alternatively, the contents of this file may be used under the
    terms of the EUPL, Version 1.1 or - as soon they will be approved
    by the European Commission - subsequent versions of the EUPL
    (the "Licence"); You may not use this work except in compliance
    with the Licence.
    You may obtain a copy of the Licence at:
    http://joinup.ec.europa.eu/software/page/eupl/licence-eupl

    Unless required by applicable law or agreed to in writing, software
    distributed under the Licence is distributed on an "AS IS" basis,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the Licence for the specific language governing permissions and
    limitations under the Licence.

    If you wish to allow use of your version of this file only
    under the terms of the EUPL License and not to allow others to use
    your version of this file under the MPL, indicate your decision by
    deleting the provisions above and replace them with the notice and
    other provisions required by the EUPL License. If you do not delete
    the provisions above, a recipient may use your version of this file
    under either the MPL or the EUPL License.

--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="de" lang="de">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>PEPPOL SML</title>
    <style type="text/css">
    body { font-family: Arial, Helvetica, sans-serif; }
    a, a:link, a:visited, a:hover, a:active { color: blue; }
    </style>
  </head>
  <body>
    <h1>PEPPOL SML waiting for you</h1>
    <ul>
    <%
      if (at.peppol.sml.server.dns.DNSClientConfiguration.isEnabled()) {
    %>
      <li><a href="listdns">List DNS items</a></li>
    <%
      }
    %>  
      <li><a href="manageparticipantidentifier">manage participant identifier</a></li>
      <li><a href="manageservicemetadata">manage service metadata</a></li>
    </ul>
    <div>Operating on the DNS zone: <b><%=at.peppol.sml.server.dns.DNSClientFactory.getInstance().getSMLZoneName()%></b></div>
    <div>Client unique ID: <b><%=at.peppol.sml.server.web.WebRequestClientIdentifier.getClientUniqueID(request)%></b></div>
  </body>
</html>

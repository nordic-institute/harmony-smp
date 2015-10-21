====
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
====

This project holds all the Busdox Types it use Jaxb and jaxws ws import to generate java objects 
from the different wsdl and xsd's used by the busdox specification
 
in Order to build this project you must use the java endorsed mechanism on the jvm used to execute the maven build 
to provide the proper versions of the jaxb and jax-ws api. To do so copy the jaxb-api-2.2.6.jar 
(http://mirrors.ibiblio.org/maven2/javax/xml/bind/jaxb-api/2.2.6/) and jaxws-api-2.2.8.jar (http://mirrors.ibiblio.org/maven2/javax/xml/ws/jaxws-api/2.2.8/)
in the $YOUR_JDK_HOME$/jre/lib/endorsed folder. For more information on the java endorsed mechanism please check :
http://docs.oracle.com/javase/7/docs/technotes/guides/standards/index.html  
 

Important issue:
This directory may not reside in a directory that contains spaces.
Otherwise the JAXB call will fail with a weird error.
This is a bug in JAXB (verified with 2.2.3-1).

Note: by adding Xerces 2.10.0 to the JAXB call, we can work around this issue!

JAXB 2.2 is required for correct WS-Addressing binding (JAXB 2.1.13 will not work!)


Philip, 2011-02-05

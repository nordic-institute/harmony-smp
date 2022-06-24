# Service Metadata Publishing

[![License badge](https://img.shields.io/badge/license-EUPL-blue.svg)](https://ec.europa.eu/digital-building-blocks/wikis/download/attachments/52601883/eupl_v1.2_en%20.pdf?version=1&modificationDate=1507206778126&api=v2)
[![Documentation badge](https://img.shields.io/badge/docs-latest-brightgreen.svg)](https://ec.europa.eu/digital-building-blocks/wikis/display/DIGITAL/SMP)
[![Support badge]( https://img.shields.io/badge/support-sof-yellowgreen.svg)](https://ec.europa.eu/digital-building-blocks/wikis/display/DIGITAL/Support+eDelivery)

		  
## Introduction

This is the code repository for eDelivery SMP, the sample implementation, open source project of the European Commission [eDelivery SMP profile](https://ec.europa.eu/digital-building-blocks/wikis/display/DIGITAL/SMP+specifications) implementation.

Any feedback on the application or the following documentation is highly welcome, including bugs, typos
or things you think should be included but aren't. You can use [JIRA](https://ec.europa.eu/digital-building-blocks/tracker/projects/EDELIVERY/issues) to provide feedback.

Following documents are available on the [Domibus release page](https://ec.europa.eu/digital-building-blocks/wikis/display/DIGITAL/SMP):
*   Administration Guide 
*   Software Architecture Document
*   Interface Control Documents

[Top](#top)

## Overall description

To successfully send a business document in a (4-corner) network, an entity must be able to discover critical metadata about 
the recipient (Access Point) of the business document, such as types of documents the Access Point is capable of receiving 
and methods of transport supported. The recipient makes this metadata available to other entities in the network through 
a Service Metadata Publisher service. The eDelivery SMP profile describes the request/response exchanges between a 
Service Metadata Publisher and a client wishing to discover Access Point metadata. The profile is based on the 
OASIS Service Metadata Publishing (SMP) Version 1.0 standard. 

The eDelivery SMP application is an implementation of the eDelivery SMP profile. The application also has a feature to 
configure the integration to SML using  [PEPPOL Transport Infrastructure SML specifications](https://docs.peppol.eu/edelivery/sml/ICT-Transport-SML_Service_Specification-101.pdf).

eDelivery SMP is the Open Source project of the AS4 Access Point maintained by the European Commission. 

If this is your first contact with the eDelivery SMP, it is highly recommended to check the [SMP Software](https://ec.europa.eu/digital-building-blocks/wikis/display/DIGITAL/SMP+software) page.

[Top](#top)

## Build

In order to build eDelivery SMP :

    mvn clean install 


[Top](#top)

## Install and run

How to install and run eDelivery SMP can be read in the Admin Guide available on the [eDelivery SMP Release Page](https://ec.europa.eu/digital-building-blocks/wikis/display/DIGITAL/SMP+software).

[Top](#top)

## License

eDelivery SMP is licensed under European Union Public Licence (EUPL) version 1.2.

[Top](#top)

## Support

Have questions? Consult our [Q&A section](https://ec.europa.eu/digital-building-blocks/wikis/display/DIGITAL/SMP+FAQs). 
Still have questions? Contact [eDelivery support](https://ec.europa.eu/digital-building-blocks/tracker/plugins/servlet/desk/portal/6).


[Top](#top)
--
-- Version: MPL 1.1/EUPL 1.1
--
-- The contents of this file are subject to the Mozilla Public License Version
-- 1.1 (the "License"); you may not use this file except in compliance with
-- the License. You may obtain a copy of the License at:
-- http://www.mozilla.org/MPL/
--
-- Software distributed under the License is distributed on an "AS IS" basis,
-- WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
-- for the specific language governing rights and limitations under the
-- License.
--
-- The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
--
-- Alternatively, the contents of this file may be used under the
-- terms of the EUPL, Version 1.1 or - as soon they will be approved
-- by the European Commission - subsequent versions of the EUPL
-- (the "Licence"); You may not use this work except in compliance
-- with the Licence.
-- You may obtain a copy of the Licence at:
-- http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the Licence is distributed on an "AS IS" basis,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the Licence for the specific language governing permissions and
-- limitations under the Licence.
--
-- If you wish to allow use of your version of this file only
-- under the terms of the EUPL License and not to allow others to use
-- your version of this file under the MPL, indicate your decision by
-- deleting the provisions above and replace them with the notice and
-- other provisions required by the EUPL License. If you do not delete
-- the provisions above, a recipient may use your version of this file
-- under either the MPL or the EUPL License.
--

DROP TABLE IF EXISTS `migrate`;
CREATE TABLE `migrate` (
  `scheme` varchar(200) NOT NULL,
  `rec_value` varchar(200) NOT NULL,
  `migration_code` varchar(200) NOT NULL,
  PRIMARY KEY (`scheme`,`rec_value`,`migration_code`)
);

DROP TABLE IF EXISTS `sml_user`;
CREATE TABLE `sml_user` (
  `sml_username` varchar(200) NOT NULL,
  `sml_password` text,
  PRIMARY KEY (`sml_username`)
);

DROP TABLE IF EXISTS `allowed_wildcard_schemes`;
CREATE TABLE `allowed_wildcard_schemes` (
  `scheme` varchar(25) NOT NULL,
  `username` varchar(200) NOT NULL,
  PRIMARY KEY (`scheme`,`username`),
  CONSTRAINT `FK_allowed_wildcard_schemes` FOREIGN KEY (`username`) REFERENCES `sml_user` (`sml_username`) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS `service_metadata_publisher`;
CREATE TABLE `service_metadata_publisher` (
  `smp_id` varchar(200) NOT NULL,
  `physical_address` text NOT NULL,
  `logical_address` text NOT NULL,
  `username` varchar(200) NOT NULL,
  PRIMARY KEY (`smp_id`),
  CONSTRAINT `FK_service_metadata_publisher` FOREIGN KEY (`username`) REFERENCES `sml_user` (`sml_username`) ON DELETE CASCADE ON UPDATE CASCADE
);



DROP TABLE IF EXISTS `recipient_part_identifier`;
CREATE TABLE `recipient_part_identifier` (
  `scheme` varchar(200) NOT NULL,
  `recvalue` varchar(200) NOT NULL,
  `smp_id` varchar(200) NOT NULL,
  PRIMARY KEY (`scheme`,`value`),
  CONSTRAINT `FK_recipient_part_identifier` FOREIGN KEY (`smp_id`) REFERENCES `service_metadata_publisher` (`smp_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

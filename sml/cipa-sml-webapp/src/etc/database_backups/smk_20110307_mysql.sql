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

CREATE DATABASE  IF NOT EXISTS `smk` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `smk`;
-- MySQL dump 10.13  Distrib 5.1.40, for Win32 (ia32)
--
-- Host: localhost    Database: smk
-- ------------------------------------------------------
-- Server version	5.1.43-community

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `migrate`
--

DROP TABLE IF EXISTS `sml_migrate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sml_migrate` (
  `recipient_participant_identifier_scheme` varchar(200) NOT NULL,
  `recipient_participant_identifier_value` varchar(200) NOT NULL,
  `migration_code` varchar(200) NOT NULL,
  PRIMARY KEY (`recipient_participant_identifier_scheme`,`recipient_participant_identifier_value`,`migration_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `migrate`
--

--
-- Table structure for table `allowed_wildcard_schemes`
--

DROP TABLE IF EXISTS `sml_allowed_wildcard_schemes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sml_allowed_wildcard_schemes` (
  `scheme` varchar(25) CHARACTER SET latin1 NOT NULL,
  `username` varchar(200) NOT NULL,
  PRIMARY KEY (`scheme`,`username`),
  KEY `new_fk_constraint` (`username`),
  CONSTRAINT `new_fk_constraint` FOREIGN KEY (`username`) REFERENCES `sml_user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `allowed_wildcard_schemes`
--

--
-- Table structure for table `service_metadata_publisher`
--

DROP TABLE IF EXISTS `sml_service_metadata_publisher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sml_service_metadata_publisher` (
  `smp_id` varchar(200) NOT NULL,
  `physical_address` text NOT NULL,
  `logical_address` text NOT NULL,
  `username` varchar(200) NOT NULL,
  PRIMARY KEY (`smp_id`),
  KEY `FK_service_metadata_publisher_1` (`username`),
  CONSTRAINT `FK_service_metadata_publisher_1` FOREIGN KEY (`username`) REFERENCES `sml_user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_metadata_publisher`
--

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `sml_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sml_user` (
  `username` varchar(200) NOT NULL,
  `password` text,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

--
-- Table structure for table `recipient_participant_identifier`
--

DROP TABLE IF EXISTS `sml_recipient_participant_identifier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sml_recipient_participant_identifier` (
  `recipient_participant_identifier_scheme` varchar(200) NOT NULL,
  `recipient_participant_identifier_value` varchar(200) NOT NULL,
  `smp_id` varchar(200) NOT NULL,
  PRIMARY KEY (`recipient_participant_identifier_scheme`,`recipient_participant_identifier_value`),
  KEY `FK_recipient_participant_identifier_1` (`smp_id`),
  CONSTRAINT `FK_recipient_participant_identifier_1` FOREIGN KEY (`smp_id`) REFERENCES `sml_service_metadata_publisher` (`smp_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recipient_participant_identifier`
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2010-12-23 19:33:33

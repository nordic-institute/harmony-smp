--
-- Copyright $today.year European Commission | CEF eDelivery
--
-- Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
-- You may not use this work except in compliance with the Licence.
--
-- You may obtain a copy of the Licence at:
-- https://joinup.ec.europa.eu/software/page/eupl
-- or file: LICENCE-EUPL-v1.1.pdf
--
-- Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the Licence for the specific language governing permissions and limitations under the Licence.

CREATE DATABASE  IF NOT EXISTS `smp` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `smp`;
-- MySQL dump 10.13  Distrib 5.1.40, for Win32 (ia32)
--
-- Host: localhost    Database: smp
-- ------------------------------------------------------
-- Server version 5.0.51a-community-nt

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
-- Not dumping tablespaces as no INFORMATION_SCHEMA.FILES table on this server
--

--
-- Table structure for table `smp_process`
--

DROP TABLE IF EXISTS `smp_process`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `smp_process` (
  `processIdentifier` varchar(256) NOT NULL,
  `processIdentifierType` varchar(256) NOT NULL,
  `extension` BLOB,
  `businessIdentifier` varchar(256) NOT NULL,
  `businessIdentifierScheme` varchar(256) NOT NULL,
  `documentIdentifier` varchar(256) NOT NULL,
  `documentIdentifierScheme` varchar(256) NOT NULL,
  PRIMARY KEY  (`processIdentifier`,`processIdentifierType`,`businessIdentifier`,`businessIdentifierScheme`,`documentIdentifier`,`documentIdentifierScheme`),
  KEY `FK_process_1` (`documentIdentifier`,`documentIdentifierScheme`,`businessIdentifier`,`businessIdentifierScheme`),
  CONSTRAINT `FK_process_1` FOREIGN KEY (`documentIdentifier`, `documentIdentifierScheme`, `businessIdentifier`, `businessIdentifierScheme`) REFERENCES `smp_service_metadata` (`documentIdentifier`, `documentIdentifierScheme`, `businessIdentifier`, `businessIdentifierScheme`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `smp_process`
--

LOCK TABLES `smp_process` WRITE;
/*!40000 ALTER TABLE `smp_process` DISABLE KEYS */;
/*!40000 ALTER TABLE `smp_process` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `smp_user`
--

DROP TABLE IF EXISTS `smp_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `smp_user` (
  `username` varchar(256) NOT NULL,
  `password` varchar(256) NOT NULL,
  PRIMARY KEY  (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `smp_user`
--

LOCK TABLES `smp_user` WRITE;
/*!40000 ALTER TABLE `smp_user` DISABLE KEYS */;
INSERT INTO `smp_user` VALUES ('peppol_user','Test1234');
/*!40000 ALTER TABLE `smp_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `smp_service_metadata_redirection`
--

DROP TABLE IF EXISTS `smp_service_metadata_redirection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `smp_service_metadata_redirection` (
  `businessIdentifier` varchar(256) NOT NULL,
  `businessIdentifierScheme` varchar(256) NOT NULL,
  `documentIdentifier` varchar(256) NOT NULL,
  `documentIdentifierScheme` varchar(256) NOT NULL,
  `redirectionUrl` varchar(256) NOT NULL,
  `extension` TEXT,
  `certificateUID` varchar(256) NOT NULL,
  PRIMARY KEY  (`businessIdentifier`,`businessIdentifierScheme`,`documentIdentifier`,`documentIdentifierScheme`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `smp_service_metadata_redirection`
--

LOCK TABLES `smp_service_metadata_redirection` WRITE;
/*!40000 ALTER TABLE `smp_service_metadata_redirection` DISABLE KEYS */;
/*!40000 ALTER TABLE `smp_service_metadata_redirection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `smp_endpoint`
--

DROP TABLE IF EXISTS `smp_endpoint`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `smp_endpoint` (
  `transportProfile` varchar(256) NOT NULL,
  `endpointReference` varchar(256) NOT NULL,
  `extension` BLOB,
  `processIdentifier` varchar(256) NOT NULL,
  `processIdentifierType` varchar(256) NOT NULL,
  `businessIdentifier` varchar(256) NOT NULL,
  `businessIdentifierScheme` varchar(256) NOT NULL,
  `documentIdentifier` varchar(256) NOT NULL,
  `documentIdentifierScheme` varchar(256) NOT NULL default '',
  `requireBusinessLevelSignature` bit(1) NOT NULL,
  `minimumAuthenticationLevel` varchar(256) default NULL,
  `serviceActivationDate` datetime default NULL,
  `serviceExpirationDate` datetime NOT NULL,
  `certificate` TEXT NOT NULL,
  `serviceDescription` BLOB NOT NULL,
  `technicalContactUrl` varchar(256) NOT NULL,
  `technicalInformationUrl` varchar(256) default NULL,
  PRIMARY KEY  (`businessIdentifierScheme`,`businessIdentifier`,`processIdentifierType`,`processIdentifier`,`documentIdentifier`,`documentIdentifierScheme`,`endpointReference`,`transportProfile`),
  KEY `FK_endpoint_1` (`processIdentifier`,`processIdentifierType`,`businessIdentifier`,`businessIdentifierScheme`,`documentIdentifier`,`documentIdentifierScheme`),
  CONSTRAINT `FK_endpoint_1` FOREIGN KEY (`processIdentifier`, `processIdentifierType`, `businessIdentifier`, `businessIdentifierScheme`, `documentIdentifier`, `documentIdentifierScheme`) REFERENCES `smp_process` (`processIdentifier`, `processIdentifierType`, `businessIdentifier`, `businessIdentifierScheme`, `documentIdentifier`, `documentIdentifierScheme`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `smp_endpoint`
--

LOCK TABLES `smp_endpoint` WRITE;
/*!40000 ALTER TABLE `smp_endpoint` DISABLE KEYS */;
/*!40000 ALTER TABLE `smp_endpoint` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `smp_ownership`
--

DROP TABLE IF EXISTS `smp_ownership`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `smp_ownership` (
  `username` varchar(256) NOT NULL,
  `businessIdentifier` varchar(256) NOT NULL,
  `businessIdentifierScheme` varchar(256) NOT NULL,
  KEY `FK_ownership_1` (`businessIdentifier`,`businessIdentifierScheme`),
  KEY `FK_ownership_2` (`username`),
  CONSTRAINT `FK_ownership_2` FOREIGN KEY (`username`) REFERENCES `smp_user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_ownership_1` FOREIGN KEY (`businessIdentifier`, `businessIdentifierScheme`) REFERENCES `smp_service_group` (`businessIdentifier`, `businessIdentifierScheme`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `smp_ownership`
--

LOCK TABLES `smp_ownership` WRITE;
/*!40000 ALTER TABLE `smp_ownership` DISABLE KEYS */;
/*!40000 ALTER TABLE `smp_ownership` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `smp_service_metadata`
--

DROP TABLE IF EXISTS `smp_service_metadata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `smp_service_metadata` (
  `documentIdentifier` varchar(256) NOT NULL,
  `documentIdentifierScheme` varchar(256) NOT NULL,
  `businessIdentifier` varchar(256) NOT NULL,
  `businessIdentifierScheme` varchar(256) NOT NULL,
  `extension` TEXT,
  PRIMARY KEY  (`documentIdentifier`,`documentIdentifierScheme`,`businessIdentifier`,`businessIdentifierScheme`),
  KEY `FK_service_metadata_1` (`businessIdentifier`,`businessIdentifierScheme`),
  CONSTRAINT `FK_service_metadata_1` FOREIGN KEY (`businessIdentifier`, `businessIdentifierScheme`) REFERENCES `smp_service_group` (`businessIdentifier`, `businessIdentifierScheme`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `smp_service_metadata`
--

LOCK TABLES `smp_service_metadata` WRITE;
/*!40000 ALTER TABLE `smp_service_metadata` DISABLE KEYS */;
/*!40000 ALTER TABLE `smp_service_metadata` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `smp_service_group`
--

DROP TABLE IF EXISTS `smp_service_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `smp_service_group` (
  `businessIdentifier` varchar(256) NOT NULL,
  `businessIdentifierScheme` varchar(256) NOT NULL,
  `extension` BLOB,
  PRIMARY KEY  (`businessIdentifier`,`businessIdentifierScheme`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `smp_service_group`
--

LOCK TABLES `smp_service_group` WRITE;
/*!40000 ALTER TABLE `smp_service_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `smp_service_group` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-02-22 19:41:35

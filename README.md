
[![License badge](https://img.shields.io/badge/license-EUPL-blue.svg)](LICENSE.md)
[![Documentation badge](https://img.shields.io/badge/docs-latest-brightgreen.svg)](https://github.com/nordic-institute/harmony-common/tree/main/doc)
[![Support badge]( https://img.shields.io/badge/support-sof-yellowgreen.svg)](https://edelivery.digital/contact)

# Harmony eDelivery Access - Service Metadata Publisher (SMP)

![Harmony eDelivery Access logo](harmony-logo.png)

## About the Repository

This repository contains the source code of the SMP component of Harmony eDelivery Access. 

Harmony eDelivery Access by [NIIS](https://niis.org) is a free and actively maintained open-source component for joining one or more eDelivery policy domains.

Harmony SMP is based on upon the [SMP](https://ec.europa.eu/cefdigital/code/projects/EDELIVERY/repos/smp) open source project by the [European Commission](https://ec.europa.eu/). 

## Documentation

The official Harmony documentation is available in a separate repository that can be found [here](https://github.com/nordic-institute/harmony-common/).

In addition, the following documents that are available on the [SMP release page](https://ec.europa.eu/cefdigital/wiki/display/CEFDIGITAL/SMP) are applicable for the Harmony SMP too:

 * Administration Guide 
 * Interface Control Documents
 * Software Architecture Document.

## Build

Harmony SMP can be built using the following command:

    mvn -f harmony-pom.xml clean install

Integration tests can be skipped using the `skipITs` property:

    mvn -f harmony-pom.xml clean install -DskipITs=true

All tests can be skipped using the `maven.test.skip` property

    mvn -f harmony-pom.xml clean install -Dmaven.test.skip=true

Full build instruction are available in the `harmony-common` [repository](https://github.com/nordic-institute/harmony-common/).

## Install and Run

Instructions to install and run Harmony SMP are available in the `harmony-common` [repository](https://github.com/nordic-institute/harmony-common/).

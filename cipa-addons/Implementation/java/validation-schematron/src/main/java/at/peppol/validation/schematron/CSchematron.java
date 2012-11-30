/**
 * Copyright (C) 2010 Bundesrechenzentrum GmbH
 * http://www.brz.gv.at
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
package at.peppol.validation.schematron;

import javax.annotation.concurrent.Immutable;

/**
 * Constants for handling Schematron documents
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class CSchematron {
  /** The namespace URL for Schematron documents */
  public static final String NAMESPACE_SCHEMATRON = "http://purl.oclc.org/dsdl/schematron";

  private CSchematron () {}
}

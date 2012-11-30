/* Copyright (c) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gdata.util.common.base;

/**
 * Utility functions for dealing with {@code CharEscaper}s, and some commonly
 * used {@code CharEscaper} instances.
 */
public final class CharEscapers {
  private CharEscapers () {}

  /**
   * Returns a {@link AbstractCharEscaper} instance that escapes special
   * characters in a string so it can safely be included in an XML document in
   * either element content or attribute values.
   * <p>
   * <b>Note</b>
   * </p>
   * : silently removes null-characters and control characters, as there is no
   * way to represent them in XML.
   */
  public static AbstractCharEscaper xmlEscaper () {
    return XML_ESCAPER;
  }

  /**
   * Escapes special characters from a string so it can safely be included in an
   * XML document in either element content or attribute values. Also removes
   * null-characters and control characters, as there is no way to represent
   * them in XML.
   */
  private static final AbstractCharEscaper XML_ESCAPER = newBasicXmlEscapeBuilder ().addEscape ('"', "&quot;")
                                                                                    .addEscape ('\'', "&apos;")
                                                                                    .toEscaper ();

  /**
   * Returns a {@link AbstractCharEscaper} instance that escapes special
   * characters in a string so it can safely be included in an XML document in
   * element content.
   * <p>
   * <b>Note</b>
   * </p>
   * : double and single quotes are not escaped, so it is not safe to use this
   * escaper to escape attribute values. Use the {@link #xmlEscaper()} escaper
   * to escape attribute values or if you are unsure. Also silently removes
   * non-whitespace control characters, as there is no way to represent them in
   * XML.
   */
  public static AbstractCharEscaper xmlContentEscaper () {
    return XML_CONTENT_ESCAPER;
  }

  /**
   * Escapes special characters from a string so it can safely be included in an
   * XML document in element content. Note that quotes are <em>not</em> escaped,
   * so <em>this is not safe for use in attribute values</em>. Use
   * {@link #XML_ESCAPER} for attribute values, or if you are unsure. Also
   * removes non-whitespace control characters, as there is no way to represent
   * them in XML.
   */
  private static final AbstractCharEscaper XML_CONTENT_ESCAPER = newBasicXmlEscapeBuilder ().toEscaper ();

  /**
   * Returns an {@link IEscaper} instance that escapes Java chars so they can be
   * safely included in URIs. For details on escaping URIs, see section 2.4 of
   * <a href="http://www.ietf.org/rfc/rfc2396.txt">RFC 2396</a>.
   * <p>
   * When encoding a String, the following rules apply:
   * <ul>
   * <li>The alphanumeric characters "a" through "z", "A" through "Z" and "0"
   * through "9" remain the same.
   * <li>The special characters ".", "-", "*", and "_" remain the same.
   * <li>The space character " " is converted into a plus sign "+".
   * <li>All other characters are converted into one or more bytes using UTF-8
   * encoding and each byte is then represented by the 3-character string "%XY",
   * where "XY" is the two-digit, uppercase, hexadecimal representation of the
   * byte value.
   * <ul>
   * <p>
   * <b>Note</b>: Unlike other escapers, URI escapers produce uppercase
   * hexidecimal sequences. From <a href="http://www.ietf.org/rfc/rfc3986.txt">
   * RFC 3986</a>:<br>
   * <i>"URI producers and normalizers should use uppercase hexadecimal digits
   * for all percent-encodings."</i>
   * <p>
   * This escaper has identical behavior to (but is potentially much faster
   * than):
   * <ul>
   * <li>{@link java.net.URLEncoder#encode(String, String)} with the encoding
   * name "UTF-8"
   * </ul>
   * <p>
   * This method is equivalent to {@code uriEscaper(true)}.
   */
  public static IEscaper uriEscaper () {
    return uriEscaper (true);
  }

  /**
   * Returns an {@link IEscaper} instance that escapes Java chars so they can be
   * safely included in URI path segments. For details on escaping URIs, see
   * section 2.4 of <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986</a>.
   * <p>
   * When encoding a String, the following rules apply:
   * <ul>
   * <li>The alphanumeric characters "a" through "z", "A" through "Z" and "0"
   * through "9" remain the same.
   * <li>The unreserved characters ".", "-", "~", and "_" remain the same.
   * <li>The general delimiters "@" and ":" remain the same.
   * <li>The subdelimiters "!", "$", "&amp;", "'", "(", ")", "*", ",", ";", and
   * "=" remain the same.
   * <li>The space character " " is converted into %20.
   * <li>All other characters are converted into one or more bytes using UTF-8
   * encoding and each byte is then represented by the 3-character string "%XY",
   * where "XY" is the two-digit, uppercase, hexadecimal representation of the
   * byte value.
   * </ul>
   * <p>
   * <b>Note</b>: Unlike other escapers, URI escapers produce uppercase
   * hexidecimal sequences. From <a href="http://www.ietf.org/rfc/rfc3986.txt">
   * RFC 3986</a>:<br>
   * <i>"URI producers and normalizers should use uppercase hexadecimal digits
   * for all percent-encodings."</i>
   */
  public static IEscaper uriPathEscaper () {
    return URI_PATH_ESCAPER;
  }

  /**
   * Returns an {@link IEscaper} instance that escapes Java chars so they can be
   * safely included in URI query string segments. When the query string
   * consists of a sequence of name=value pairs separated by &amp;, the names
   * and values should be individually encoded. If you escape an entire query
   * string in one pass with this escaper, then the "=" and "&amp;" characters
   * used as separators will also be escaped.
   * <p>
   * This escaper is also suitable for escaping fragment identifiers.
   * <p>
   * For details on escaping URIs, see section 2.4 of <a
   * href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986</a>.
   * <p>
   * When encoding a String, the following rules apply:
   * <ul>
   * <li>The alphanumeric characters "a" through "z", "A" through "Z" and "0"
   * through "9" remain the same.
   * <li>The unreserved characters ".", "-", "~", and "_" remain the same.
   * <li>The general delimiters "@" and ":" remain the same.
   * <li>The path delimiters "/" and "?" remain the same.
   * <li>The subdelimiters "!", "$", "'", "(", ")", "*", ",", and ";", remain
   * the same.
   * <li>The space character " " is converted into %20.
   * <li>The equals sign "=" is converted into %3D.
   * <li>The ampersand "&amp;" is converted into %26.
   * <li>All other characters are converted into one or more bytes using UTF-8
   * encoding and each byte is then represented by the 3-character string "%XY",
   * where "XY" is the two-digit, uppercase, hexadecimal representation of the
   * byte value.
   * </ul>
   * <p>
   * <b>Note</b>: Unlike other escapers, URI escapers produce uppercase
   * hexidecimal sequences. From <a href="http://www.ietf.org/rfc/rfc3986.txt">
   * RFC 3986</a>:<br>
   * <i>"URI producers and normalizers should use uppercase hexadecimal digits
   * for all percent-encodings."</i>
   */
  public static IEscaper uriQueryStringEscaper () {
    return URI_QUERY_STRING_ESCAPER;
  }

  /**
   * Returns a {@link IEscaper} instance that escapes Java characters so they
   * can be safely included in URIs. For details on escaping URIs, see section
   * 2.4 of <a href="http://www.ietf.org/rfc/rfc2396.txt">RFC 2396</a>.
   * <p>
   * When encoding a String, the following rules apply:
   * <ul>
   * <li>The alphanumeric characters "a" through "z", "A" through "Z" and "0"
   * through "9" remain the same.
   * <li>The special characters ".", "-", "*", and "_" remain the same.
   * <li>If {@code plusForSpace} was specified, the space character " " is
   * converted into a plus sign "+". Otherwise it is converted into "%20".
   * <li>All other characters are converted into one or more bytes using UTF-8
   * encoding and each byte is then represented by the 3-character string "%XY",
   * where "XY" is the two-digit, uppercase, hexadecimal representation of the
   * byte value.
   * </ul>
   * <p>
   * <b>Note</b>: Unlike other escapers, URI escapers produce uppercase
   * hexidecimal sequences. From <a href="http://www.ietf.org/rfc/rfc3986.txt">
   * RFC 3986</a>:<br>
   * <i>"URI producers and normalizers should use uppercase hexadecimal digits
   * for all percent-encodings."</i>
   * 
   * @param plusForSpace
   *        if {@code true} space is escaped to {@code +} otherwise it is
   *        escaped to {@code %20}. Although common, the escaping of spaces as
   *        plus signs has a very ambiguous status in the relevant
   *        specifications. You should prefer {@code %20} unless you are doing
   *        exact character-by-character comparisons of URLs and backwards
   *        compatibility requires you to use plus signs.
   * @see #uriEscaper()
   */
  public static IEscaper uriEscaper (final boolean plusForSpace) {
    return plusForSpace ? URI_ESCAPER : URI_ESCAPER_NO_PLUS;
  }

  private static final IEscaper URI_ESCAPER = new PercentEscaper (PercentEscaper.SAFECHARS_URLENCODER, true);

  private static final IEscaper URI_ESCAPER_NO_PLUS = new PercentEscaper (PercentEscaper.SAFECHARS_URLENCODER, false);

  private static final IEscaper URI_PATH_ESCAPER = new PercentEscaper (PercentEscaper.SAFEPATHCHARS_URLENCODER, false);

  private static final IEscaper URI_QUERY_STRING_ESCAPER = new PercentEscaper (PercentEscaper.SAFEQUERYSTRINGCHARS_URLENCODER,
                                                                               false);

  private static CharEscaperBuilder newBasicXmlEscapeBuilder () {
    return new CharEscaperBuilder ().addEscape ('&', "&amp;")
                                    .addEscape ('<', "&lt;")
                                    .addEscape ('>', "&gt;")
                                    .addEscapes (new char [] { '\000',
                                                              '\001',
                                                              '\002',
                                                              '\003',
                                                              '\004',
                                                              '\005',
                                                              '\006',
                                                              '\007',
                                                              '\010',
                                                              '\013',
                                                              '\014',
                                                              '\016',
                                                              '\017',
                                                              '\020',
                                                              '\021',
                                                              '\022',
                                                              '\023',
                                                              '\024',
                                                              '\025',
                                                              '\026',
                                                              '\027',
                                                              '\030',
                                                              '\031',
                                                              '\032',
                                                              '\033',
                                                              '\034',
                                                              '\035',
                                                              '\036',
                                                              '\037' },
                                                 "");
  }
}

/*
 * Copyright (C) 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gdata.util.common.base;

import java.util.NoSuchElementException;

/**
 * Simple static methods to be called at the start of your own methods to verify
 * correct arguments and state. This allows constructs such as
 * 
 * <pre>
 * if (count &lt;= 0) {
 *   throw new IllegalArgumentException (&quot;must be positive: &quot; + count);
 * }
 * </pre>
 * 
 * to be replaced with the more compact
 * 
 * <pre>
 * checkArgument (count &gt; 0, &quot;must be positive: %s&quot;, count);
 * </pre>
 * 
 * Note that the sense of the expression is inverted; with {@code Preconditions}
 * you declare what you expect to be <i>true</i>, just as you do with an <a
 * href="http://java.sun.com/j2se/1.5.0/docs/guide/language/assert.html">
 * {@code assert}</a> or a JUnit {@code assertTrue()} call.
 * <p>
 * Take care not to confuse precondition checking with other similar types of
 * checks! Precondition exceptions -- including those provided here, but also
 * {@link IndexOutOfBoundsException}, {@link NoSuchElementException},
 * {@link UnsupportedOperationException} and others -- are used to signal that
 * the <i>calling method</i> has made an error. This tells the caller that it
 * should not have invoked the method when it did, with the arguments it did, or
 * perhaps <i>ever</i>. Postcondition or other invariant failures should not
 * throw these types of exceptions.
 * <p>
 * <b>Note:</b> The methods of the {@code Preconditions} class are highly
 * unusual in one way: they are <i>supposed to</i> throw exceptions, and promise
 * in their specifications to do so even when given perfectly valid input. That
 * is, {@code null} is a valid parameter to the method
 * {@link #checkNotNull(Object)} -- and technically this parameter could be even
 * marked as {@link javax.annotation.Nullable} -- yet the method will still
 * throw an exception anyway, because that's what its contract says to do.
 */
public final class Preconditions {
  private Preconditions () {}

  /**
   * Ensures that an object reference passed as a parameter to the calling
   * method is not null.
   * 
   * @param reference
   *        an object reference
   * @return the non-null reference that was validated
   * @throws NullPointerException
   *         if {@code reference} is null
   */
  public static <T> T checkNotNull (final T reference) {
    if (reference == null) {
      throw new NullPointerException ();
    }
    return reference;
  }
}

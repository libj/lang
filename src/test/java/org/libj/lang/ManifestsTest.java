/* Copyright (c) 2021 LibJ
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * You should have received a copy of The MIT License (MIT) along with this
 * program. If not, see <http://opensource.org/licenses/MIT/>.
 */

package org.libj.lang;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class ManifestsTest {
  @Test
  public void testGetManifest() throws IOException {
    try {
      Manifests.getManifest(null);
      fail("Expected NullPointerException");
    }
    catch (final NullPointerException e) {
    }

    Manifests.getManifest(ManifestsTest.class);
  }

  @Test
  public void testGetManifests() throws IOException {
    try {
      Manifests.getManifests(null);
      fail("Expected NullPointerException");
    }
    catch (final NullPointerException e) {
    }

    Manifests.getManifests(ManifestsTest.class);
  }
}
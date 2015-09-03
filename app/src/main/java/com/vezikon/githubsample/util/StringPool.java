package com.vezikon.githubsample.util;


/*******************************************************************************
 * Copyright (c) 2009, 2014 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Brock Janiczak - analysis and concept
 *    Marc R. Hoffmann - initial API and implementation
 *
 *******************************************************************************/

import java.util.HashMap;


        import java.util.HashMap;
        import java.util.Map;

/**
 * Utility to normalize {@link String} instances in a way that if
 * <code>equals()</code> is <code>true</code> for two strings they will be
 * represented the same instance. While this is exactly what
 * {@link String#intern()} does, this implementation avoids VM specific side
 * effects and is supposed to be faster, as neither native code is called nor
 * synchronization is required for concurrent lookup.
 */
public final class StringPool {

    private final String[] pool = new String[512];

    private static boolean contentEquals(String s, char[] chars, int start, int length) {
        if (s.length() != length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (chars[start + i] != s.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a string equal to {@code new String(array, start, length)}.
     */
    public String get(char[] array, int start, int length) {
        // Compute an arbitrary hash of the content
        int hashCode = 0;
        for (int i = start; i < start + length; i++) {
            hashCode = (hashCode * 31) + array[i];
        }

        // Pick a bucket using Doug Lea's supplemental secondaryHash function (from HashMap)
        hashCode ^= (hashCode >>> 20) ^ (hashCode >>> 12);
        hashCode ^= (hashCode >>> 7) ^ (hashCode >>> 4);
        int index = hashCode & (pool.length - 1);

        String pooled = pool[index];
        if (pooled != null && contentEquals(pooled, array, start, length)) {
            return pooled;
        }

        String result = new String(array, start, length);
        pool[index] = result;
        return result;
    }
}
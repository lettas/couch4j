/*
 * The MIT License
 *
 * Copyright (c) 2009, 2010 Stefan Saasen
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
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.couch4j.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Collection utility methods.
 * 
 * @author Stefan Saasen
 */
public final class CollectionUtils {
    
    private CollectionUtils() {
        throw new AssertionError("CollectionUtils should not be instantiated");
    }
    
    /**
     * Use import static org.couch4j.util.CollectionUtils.set;
     * 
     * <pre>
     * Map<String,String> m = map("key1", "value1", "key2", "value2");
     * 
     * m => {key1: value1, key2: value2}
     * </pre>
     * 
     * @param <T>
     * @param input, even number of elements. Elements should be key => value pairs.
     * @return Map based on the input arguments.
     */
    public static <T> Map<T, T> map(T... input) {
        if (null == input || input.length < 1) {
            return Collections.emptyMap();
        }
        if ((input.length & 1) == 1) {
            throw new IllegalArgumentException(
                    "Input has to contain an even number of arguments: 'key1', 'value1', 'key2', 'values'...");
        }
        HashMap<T, T> m = new HashMap<T, T>();
        for (Iterator<T> iterator = Arrays.asList(input).iterator(); iterator.hasNext();) {
            T key = iterator.next();
            T value = iterator.next();
            m.put(key, value);
        }
        return m;
    }
    
    /**
     * Use import static org.couch4j.util.CollectionUtils.set;
     * 
     * <p>
     * List<String> hs = list("s1", "s2");
     * </p>
     * 
     * @param <T>
     * @param input
     * @return List based on the input arguments.
     */
    public static <T> List<T> list(T... input) {
        return Arrays.asList(input);
    }    
    
    /**
     * Use import static org.couch4j.util.CollectionUtils.set;
     * 
     * <p>
     * Set<String> hs = set("s1", "s2");
     * </p>
     * 
     * @param <T>
     * @param input
     * @return Set based on the input arguments.
     */
    public static <T> Set<T> set(T... input) {
        return new HashSet<T>(Arrays.asList(input));
    }

    /**
     * Use import static org.couch4j.util.CollectionUtils.ary;
     * 
     * <p>
     * Integer[] aryOfInts = ary(1,2,3,4);
     * </p>
     * @param <T>
     * @param input
     * @return
     */
    public static <T> T[] ary(T... input) {
        return input;
    }
    
    /**
     * Checks if the given array includes the given value.
     *  
     * <p>
     * String[] rigs = ary("javelin", "atom", "mirage");
     * 
     * if(CollectionUtils.includes(rigs, "mirage")) {
     *     System.out.println("Mirage available!");
     * }
     * </p>
     * 
     * @param <T>
     *
     * @param ary The array 
     * @param value The value to check for in the given array {@code ary}.
     * @return true if ary includes value, false otherwise.
     */
    public static <T> boolean includes(T[] ary, T value) {
        if(null == ary) {
            throw new IllegalArgumentException("The supplied array is null.");
        }
        for(T elem : ary) {
            if(null == elem) {
                continue;
            }
            if(elem.equals(value)) {
                return true;
            }
        }
        return false;
    }    
}

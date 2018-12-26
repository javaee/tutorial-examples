/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014-2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package javaeetutorial.producermethods;

/**
 * Implementation of the Coder interface that shifts letters in a string.
 */
public class CoderImpl implements Coder {

    /**
     * Shifts an input string by the number of characters
     * specified in the second argument.
     *
     * @param s     the input string
     * @param tval  the number of characters to shift
     * @return      the transformed string
     */
    @Override
    public String codeString(String s, int tval) {
        final int SPACE_CHAR = 32;
        final int CAPITAL_A = 65;
        final int CAPITAL_Z = 90;
        final int SMALL_A = 97;
        final int SMALL_Z = 122;

        StringBuilder sb = new StringBuilder(s);

        for (int i = 0; i < sb.length(); i++) {
            int transposval = tval;

            int cp = sb.codePointAt(i);
            int cplus = cp + transposval;
            if (cp == SPACE_CHAR) //space
            {
                cplus = SPACE_CHAR;
            }
            if ((cp >= CAPITAL_A) && (cp <= CAPITAL_Z)) { //uppercase
                if (cplus > CAPITAL_Z) {
                    cplus = cplus - 26;
                }
            } else if ((cp >= SMALL_A) && (cp <= SMALL_Z)) { //lowercase
                if (cplus > SMALL_Z) {
                    cplus = cplus - 26;
                }
            } else { // punctuation, etc.
                cplus = cp;
            }
            char c = (char) cplus;
            sb.setCharAt(i, c);
        }

        return (sb.toString());

    }
}

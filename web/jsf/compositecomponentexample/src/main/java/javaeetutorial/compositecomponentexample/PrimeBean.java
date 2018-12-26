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

package javaeetutorial.compositecomponentexample;

import java.io.Serializable;
import java.math.BigInteger;
import javax.enterprise.inject.Model;
import javax.validation.constraints.Size;

@Model
public class PrimeBean implements Serializable {

    private static final long serialVersionUID = -50939649434906127L;
    private static int[] primes;
    @Size(min=1, max=45)
    private String name;
    private boolean prime;
    private String response;
    private int totalSum;

    /**
     * Creates a new instance of PrimeBean
     */
    public PrimeBean() {
        setPrimes();
    }

    /**
     * Sum up the letter values, then determine if the sum is prime.
     * 
     * @return String the index page
     */
    public String calculate() {
        final String letters;
        letters = "abcdefghijklmnopqrstuvwxyz";
        int sum = 0;
        for (int m = 0; m < name.length(); m++) {
            char let = name.charAt(m);
            for (int n = 0; n < 26; n++) {
                char tc = Character.toLowerCase(let);
                if (tc != letters.charAt(n)) {
                } else {
                    int letVal = n + 1;
                    System.out.println("Letter value of " + let
                            + " is " + letVal);
                    sum += letVal;
                }
            }
        }
        System.out.println("Sum is " + sum);
        prime = false;
        if (sum == 0) {
            setResponse("String contains no letters");
        } else if (sum % 2 == 0 && sum != 2) {
            setResponse("Sum of letters is not prime");
        } else if (sum % 3 == 0 && sum != 3) {
            setResponse("Sum of letters is not prime");
        } else {
            for (int n = 0; n < 194; n++) {
                if (sum != primes[n]) {
                } else {
                    prime = true;
                }
            }
            if (prime) {
                setResponse("Sum of letters is prime");
            } else {
                setResponse("Sum of letters is not prime");
            }
        }
        totalSum = sum;
        return "index";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPrime() {
        return prime;
    }

    public void setPrime(boolean prime) {
        this.prime = prime;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public int getTotalSum() {
        return totalSum;
    }

    public void setTotalSum(int totalSum) {
        this.totalSum = totalSum;
    }

    public int[] getPrimes() {
        return primes;
    }

    /**
     * Creates an array of all primes up through 1171 (one greater than
     * the sum of 45 Z's, since the maximum string length is 45)
     */
    public static void setPrimes() {
        BigInteger i;
        BigInteger lastNum;
        int count = 0;

        primes = new int[194];
        i = new BigInteger("1");
        lastNum = new BigInteger("1171");
        do {
            primes[count] = i.intValue();
            i = i.nextProbablePrime();
            count++;
        } while (i.compareTo(lastNum) <= 0x0);
    }
}

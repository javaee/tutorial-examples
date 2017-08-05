/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
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

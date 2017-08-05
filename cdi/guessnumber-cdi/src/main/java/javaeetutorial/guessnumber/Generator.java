/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.guessnumber;

import java.io.Serializable;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class Generator implements Serializable {

   private static final long serialVersionUID = -7213673465118041882L;

   private final java.util.Random random = 
       new java.util.Random( System.currentTimeMillis() );

   private final int maxNumber = 100;

   java.util.Random getRandom() {
       return random;
   }

   @Produces @Random int next() {
       return getRandom().nextInt(maxNumber + 1);
   }

   @Produces @MaxNumber int getMaxNumber() {
       return maxNumber;
   }

}


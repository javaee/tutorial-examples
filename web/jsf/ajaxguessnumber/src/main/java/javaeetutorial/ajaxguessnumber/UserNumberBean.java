/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.ajaxguessnumber;

import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@RequestScoped
public class UserNumberBean implements Serializable {

    private static final long serialVersionUID = 5377607515424187221L;
    @Inject
    DukesNumberBean dukesNumberBean;
    private Integer userNumber = null;
    String response = null;

    public void setUserNumber(Integer user_number) {
        userNumber = user_number;
    }

    public Integer getUserNumber() {
        return userNumber;
    }

    public String getResponse() {
        if ((userNumber != null)
                && (userNumber.compareTo(dukesNumberBean.getRandomInt()) == 0)) {
            return "Yay! You got it!";
        }
        if (userNumber == null) {
            return null;
        } else {
            return "Sorry, " + userNumber + " is incorrect.";
        }
    }
}

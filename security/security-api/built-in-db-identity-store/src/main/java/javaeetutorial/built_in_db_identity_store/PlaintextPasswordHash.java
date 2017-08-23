/**
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
 
package javaeetutorial.built_in_db_identity_store;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.security.enterprise.identitystore.PasswordHash;

/**
 * Plain text password hash, being defined here just to demonstrate how PasswordHash
 * can be implemented,should never be used in a production setting or even in legacy environment.
 */
@Dependent
public class PlaintextPasswordHash implements PasswordHash {

    @Override
    public void initialize(Map<String, String> parameters) {

    }

    @Override
    public String generate(char[] password) {
        return new String(password);
    }

    @Override
    public boolean verify(char[] password, String hashedPassword) {
         //don't bother with constant time comparison; more portable
         //this way, and algorithm will be used only for testing.
        return (password != null && password.length > 0 &&
                hashedPassword != null && hashedPassword.length() > 0 &&
                hashedPassword.equals(new String(password)));
    }
}
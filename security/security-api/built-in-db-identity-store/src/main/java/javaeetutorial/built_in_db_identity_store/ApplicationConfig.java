/**
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */

package javaeetutorial.built_in_db_identity_store;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;

// Database Definition for built-in DatabaseIdentityStore
@DatabaseIdentityStoreDefinition(
    dataSourceLookup="${'jdbc/__default'}", // points to database bundled with Glassfish
    callerQuery="#{'select password from caller where name = ?'}",
    groupsQuery="select group_name from caller_groups where caller_name = ?",
    hashAlgorithm = PlaintextPasswordHash.class,
    hashAlgorithmParameters = {
        "foo=bar", 
        "kax=zak", 
        "foox=${'iop'}",
        "${applicationConfig.dyna}"
        
    } // just for test / example
)
@ApplicationScoped
@Named
public class ApplicationConfig {
    
    public String[] getDyna() {
        return new String[] {"dyn=1","dyna=2","dynam=3"};
    }
    
}

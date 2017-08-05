/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package javaeetutorial.dukestutoring.web;

import javaeetutorial.dukestutoring.util.StatusType;
import java.util.Locale;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author ievans
 */
@Named
@RequestScoped
public class StatusManager {
    
    private final FacesContext ctx = FacesContext.getCurrentInstance();
    private final Locale locale;

    /** Creates a new instance of StatusManager */
    public StatusManager() {
        locale = ctx.getViewRoot().getLocale();
    }
    
    public String getLocalizedStatus(StatusType status) {
        return status.toString(locale);
    }

}

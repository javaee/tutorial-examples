/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.dukesbookstore.listeners;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;

/**
 * <p>
 * Value change listener for the name entered on the
 * <code>bookcashier.xhtml</code> page.</p>
 */
public class NameChanged extends Object implements ValueChangeListener {

    private static final Logger logger = 
            Logger.getLogger("dukesbookstore.listeners.NameChanged");

    @Override
    public void processValueChange(ValueChangeEvent event)
            throws AbortProcessingException {
        logger.log(Level.INFO, "Entering NameChanged.processValueChange");
        if (null != event.getNewValue()) {
            FacesContext.getCurrentInstance().getExternalContext()
                    .getSessionMap().put("name", event.getNewValue());
        }
    }
}

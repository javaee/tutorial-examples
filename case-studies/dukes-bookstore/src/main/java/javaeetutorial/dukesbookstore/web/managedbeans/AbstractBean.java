/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.dukesbookstore.web.managedbeans;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * <p>Abstract base class for managed beans to share utility methods.</p>
 */
@Named
@SessionScoped
public class AbstractBean implements Serializable {

    private static final long serialVersionUID = -3375564172975657665L;
    @Inject
    ShoppingCart cart;

    /**
     * @return the <code>FacesContext</code> instance for the current request.
     */
    protected FacesContext context() {
        return (FacesContext.getCurrentInstance());
    }

    /**
     * <p>Add a localized message to the
     * <code>FacesContext</code> for the current request.</p>
     *
     * @param clientId Client identifier of the component this message relates
     * to, or <code>null</code> for global messages
     * @param key Message key of the message to include
     */
    protected void message(String clientId, String key) {
        // Look up the requested message text
        String text;

        try {
            ResourceBundle bundle = ResourceBundle.getBundle(
                    "javaeetutorial.dukesbookstore.web.messages.Messages",
                    context().getViewRoot().getLocale());
            text = bundle.getString(key);
        } catch (Exception e) {
            text = "???" + key + "???";
        }

        // Construct and add a FacesMessage containing it
        context().addMessage(clientId, new FacesMessage(text));
    }

    /**
     * <p>Add a localized message to the
     * <code>FacesContext</code> for the current request.</p>
     *
     * @param clientId Client identifier of the component this message relates
     * to, or <code>null</code> for global messages
     * @param key Message key of the message to include
     * @param params Substitution parameters for using the localized text as a
     * message format
     */
    protected void message(String clientId, String key, Object[] params) {
        // Look up the requested message text
        String text;

        try {
            ResourceBundle bundle = ResourceBundle.getBundle(
                    "javaeetutorial.dukesbookstore.web.messages.Messages",
                    context().getViewRoot().getLocale());
            text = bundle.getString(key);
        } catch (Exception e) {
            text = "???" + key + "???";
        }

        // Perform the requested substitutions
        if ((params != null) && (params.length > 0)) {
            text = MessageFormat.format(text, params);
        }

        // Construct and add a FacesMessage containing it
        context().addMessage(clientId, new FacesMessage(text));
    }
}

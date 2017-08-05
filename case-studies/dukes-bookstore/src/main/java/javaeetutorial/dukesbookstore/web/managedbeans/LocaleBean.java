/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.dukesbookstore.web.managedbeans;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 * <p>Managed bean that retrieves current locale, used on each page.</p>
 */
@Named
@SessionScoped
public class LocaleBean extends AbstractBean implements Serializable {

    private static final Logger logger =
            Logger.getLogger("dukesbookstore.web.managedbeans.LocaleBean");
    private static final long serialVersionUID = -2181710426297811604L;
    private Locale locale =
            context().getViewRoot().getLocale();

    public LocaleBean() {
    }

    public Locale getLocale() {
        logger.log(Level.INFO, "Entering LocaleBean.getLocale");
        return locale;
    }

    public void setLocale(Locale locale) {
        logger.log(Level.INFO, "Entering LocaleBean.setLocale");
        this.locale = locale;
    }

    public String getLanguage() {
        Locale newlocale = null;
        logger.log(Level.INFO, "Entering LocaleBean.getLanguage");
        String lang = locale.getLanguage();
        Map map = context().getExternalContext().getSessionMap();
        if (map.containsKey("locale")) {
            newlocale = (Locale) map.get("locale");
        }
        if (!(newlocale == null)) {
            String newlang = newlocale.getLanguage();
            if (!newlang.equals(lang)) {
                return newlang;
            }
        }
        return lang;
    }

    public void setLanguage(String language) {
        logger.log(Level.INFO, "Entering LocaleBean.setLanguage");
        locale = new Locale(language);
        context().getViewRoot().setLocale(locale);
    }
}

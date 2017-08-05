/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.checkoutmodule;

import java.io.Serializable;
import javax.inject.Named;
import javax.faces.flow.FlowScoped;
import javax.faces.model.SelectItem;

/**
 * Backing bean for JoinFlow.
 */
@Named
@FlowScoped("joinFlow")
public class JoinFlowBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private boolean fanClub;
    private String[] newsletters;
    private static final SelectItem[] newsletterItems = {
        new SelectItem("Duke's Quarterly"),
        new SelectItem("Innovator's Almanac"),
        new SelectItem("Duke's Diet and Exercise Journal"),
        new SelectItem("Random Ramblings")
    };

    public JoinFlowBean() {
        this.newsletters = new String[0];
    }

    public String getReturnValue() {
        return "/exithome";
    }

    public boolean isFanClub() {
        return fanClub;
    }

    public void setFanClub(boolean fanClub) {
        this.fanClub = fanClub;
    }

    public String[] getNewsletters() {
        return newsletters;
    }

    public void setNewsletters(String[] newsletters) {
        this.newsletters = newsletters;
    }

    public SelectItem[] getNewsletterItems() {
        return newsletterItems;
    }

}

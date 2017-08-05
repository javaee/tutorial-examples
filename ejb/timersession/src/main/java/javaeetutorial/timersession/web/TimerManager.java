/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.timersession.web;

import java.io.Serializable;
import javaeetutorial.timersession.ejb.TimerSessionBean;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author ian
 */
@Named
@SessionScoped
public class TimerManager implements Serializable {

    @EJB
    private TimerSessionBean timerSession;

    private String lastProgrammaticTimeout;
    private String lastAutomaticTimeout;

    /** Creates a new instance of TimerManager */
    public TimerManager() {
        this.lastProgrammaticTimeout = "never";
        this.lastAutomaticTimeout = "never";
    }

    /**
     * @return the lastTimeout
     */
    public String getLastProgrammaticTimeout() {
        lastProgrammaticTimeout = timerSession.getLastProgrammaticTimeout();
        return lastProgrammaticTimeout;
    }

    /**
     * @param lastTimeout the lastTimeout to set
     */
    public void setLastProgrammaticTimeout(String lastTimeout) {
        this.lastProgrammaticTimeout = lastTimeout;
    }

    public void setTimer() {
        long timeoutDuration = 8000;
        timerSession.setTimer(timeoutDuration);
    }

    /**
     * @return the lastAutomaticTimeout
     */
    public String getLastAutomaticTimeout() {
        lastAutomaticTimeout = timerSession.getLastAutomaticTimeout();
        return lastAutomaticTimeout;
    }

    /**
     * @param lastAutomaticTimeout the lastAutomaticTimeout to set
     */
    public void setLastAutomaticTimeout(String lastAutomaticTimeout) {
        this.lastAutomaticTimeout = lastAutomaticTimeout;
    }

}

/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package com.forest.shipment.web;

import com.forest.entity.Groups;
import com.forest.entity.Person;
import com.forest.qualifiers.LoggedIn;
import com.forest.shipment.session.UserBean;
import com.forest.shipment.web.util.JsfUtil;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * UserController is an authorization controller responsible 
 * for user login/logout actions
 * @author markito
 */
@Named(value = "userController")
@SessionScoped
public class UserController implements Serializable {
    
    private static final long serialVersionUID = -7440104826420270291L;
    
    @Inject
    ShippingBean adapter;
    
    Person user;
    @EJB
    private UserBean ejbFacade;
    private String username;
    private String password;

    /**
     * Login method based on <code>HttpServletRequest</code> and security realm
     */
    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        String result;

        try {
            request.login(this.getUsername(), this.getPassword());
            

            this.user = ejbFacade.getUserByEmail(getUsername());
            this.getAuthenticatedUser();

            if (isAdmin(user)) {
                result = "/admin/index";
                JsfUtil.addSuccessMessage("Login Success! Welcome back!");

            } else {
                 JsfUtil.addErrorMessage("You're not a system administrator and cannot access this page.");
                 result = this.logout();
            }
            
        } catch (ServletException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
            
            //TODO: add message to resources bundle
            JsfUtil.addErrorMessage("Invalid user or password. Login invalid!");
            result = "/login";
        }
        
        return result;
    }
    
    public boolean isAdmin(Person user) {   
        for (Groups g : user.getGroupsList()) {
                if (g.getName().equals("ADMINS")) {
                    return true;
                }
            }
        return false;
    }
    
    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            this.user = null;
            
            ((HttpSession) context.getExternalContext().getSession(false)).invalidate();
            request.logout();
            //TODO: add message to resources bundle
            JsfUtil.addSuccessMessage("User successfully logged out! ");
            
        } catch (ServletException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
            //TODO: add message to resources bundle
            JsfUtil.addErrorMessage("Critical error during logout process.");
        } 
        
        return "/index";
    }

    /**
     * @return the ejbFacade
     */
    public UserBean getEjbFacade() {
        return ejbFacade;
    }

    public @Produces
    @LoggedIn
    Person getAuthenticatedUser() {
        return user;
    }

    public boolean isLogged() {        
        return getUser() == null ? false : true;
    }
    
    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the user
     */
    public Person getUser() {
        return user;
    }
}

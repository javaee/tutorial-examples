/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package com.forest.web;

import com.forest.ejb.OrderBean;
import com.forest.ejb.OrderJMSManager;
import com.forest.entity.CustomerOrder;
import com.forest.entity.Person;
import com.forest.qualifiers.LoggedIn;
import com.forest.web.util.AbstractPaginationHelper;
import com.forest.web.util.JsfUtil;
import com.forest.web.util.PageNavigation;
import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

@Named(value = "customerOrderController")
@SessionScoped
public class CustomerOrderController implements Serializable {

    private static final String BUNDLE = "bundles.Bundle";
    private static final long serialVersionUID = 8606060319870740714L;
    @Inject
    @LoggedIn
    private Person user;
    private List<CustomerOrder> myOrders;
    private CustomerOrder current;
    private DataModel items = null;
    @EJB
    private com.forest.ejb.OrderBean ejbFacade;
    @EJB
    private OrderJMSManager orderJMSManager;
    private AbstractPaginationHelper pagination;
    private int selectedItemIndex;
    private String searchString;
    private static final Logger logger = Logger.getLogger(CustomerOrderController.class.getCanonicalName());

    public CustomerOrderController() {
    }

    public CustomerOrder getSelected() {
        if (current == null) {
            current = new CustomerOrder();
            selectedItemIndex = -1;
        }
        return current;
    }

    private OrderBean getFacade() {
        return ejbFacade;
    }

    public AbstractPaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new AbstractPaginationHelper(10) {
                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public PageNavigation prepareList() {
        recreateModel();
        return PageNavigation.LIST;
    }

    public PageNavigation prepareView() {
        current = (CustomerOrder) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return PageNavigation.VIEW;
    }

    public PageNavigation prepareCreate() {
        current = new CustomerOrder();
        selectedItemIndex = -1;
        return PageNavigation.CREATE;
    }

    public PageNavigation create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("CustomerOrderCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle(BUNDLE).getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public PageNavigation prepareEdit() {
        current = (CustomerOrder) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return PageNavigation.EDIT;
    }

    public PageNavigation update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("CustomerOrderUpdated"));
            return PageNavigation.VIEW;
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle(BUNDLE).getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public PageNavigation destroy() {
        current = (CustomerOrder) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreateModel();
        return PageNavigation.LIST;
    }

    public PageNavigation cancelOrder() {
        current = (CustomerOrder) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();

        try {
            // remove from JMS queue
            orderJMSManager.deleteMessage(current.getId());

            // update DB order status
            ejbFacade.setOrderStatus(current.getId(), String.valueOf(OrderBean.Status.CANCELLED_MANUAL.getStatus()));


            recreateModel();
            return PageNavigation.LIST;
        } catch (Exception ex) {
            
            ex.printStackTrace();
        }

        return PageNavigation.INDEX;
    }

    public List<CustomerOrder> getMyOrders() {

        if (user != null) {

            myOrders = getFacade().getOrderByCustomerId(user.getId());
            if (myOrders.isEmpty()) {

                logger.log(Level.FINEST, "Customer {0} has no orders to display.", user.getEmail());
                return null;
            } else {
                logger.log(Level.FINEST, "Order amount:{0}", myOrders.get(0).getAmount());
                return myOrders;
            }

        } else {

            JsfUtil.addErrorMessage("Current user is not authenticated. Please do login before accessing your orders.");

            return null;
        }
    }

    public PageNavigation destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return PageNavigation.VIEW;
        } else {
            // all items were removed - go back to list
            recreateModel();
            return PageNavigation.LIST;
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);

            JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("CustomerOrderDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle(BUNDLE).getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    public PageNavigation next() {
        getPagination().nextPage();
        recreateModel();
        return PageNavigation.LIST;
    }

    public PageNavigation previous() {
        getPagination().previousPage();
        recreateModel();
        return PageNavigation.LIST;
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    /**
     * @return the searchString
     */
    public String getSearchString() {
        return searchString;
    }

    /**
     * @param searchString the searchString to set
     */
    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    @FacesConverter(forClass = CustomerOrder.class)
    public static class CustomerOrderControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CustomerOrderController controller = (CustomerOrderController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "customerOrderController");
            return controller.ejbFacade.find(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof CustomerOrder) {
                CustomerOrder o = (CustomerOrder) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + CustomerOrderController.class.getName());
            }
        }
    }
}

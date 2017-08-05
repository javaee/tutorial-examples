/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.dukesbookstore.web.managedbeans;

import java.util.Calendar;
import java.util.Date;
import javaeetutorial.dukesbookstore.ejb.BookRequestBean;
import javaeetutorial.dukesbookstore.exception.OrderException;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIOutput;
import javax.faces.component.UISelectBoolean;
import javax.faces.model.SelectItem;
import javax.inject.Named;

/**
 * <p>Backing bean for the <code>/bookcashier.xhtml</code> and
 * <code>bookreceipt.xhtml</code> pages.</p>
 */
@Named
@RequestScoped
public class CashierBean extends AbstractBean {

    private static final long serialVersionUID = -9221440716172304017L;
    @EJB
    BookRequestBean bookRequestBean;
    private String name = null;
    private String creditCardNumber = null;
    private Date shipDate;
    private String shippingOption = "2";
    UIOutput specialOfferText = null;
    UISelectBoolean specialOffer = null;
    UIOutput thankYou = null;
    private String stringProperty = "This is a String property";
    private String[] newsletters;
    private static final SelectItem[] newsletterItems = {
        new SelectItem("Duke's Quarterly"),
        new SelectItem("Innovator's Almanac"),
        new SelectItem("Duke's Diet and Exercise Journal"),
        new SelectItem("Random Ramblings")
    };

    public CashierBean() {
        this.newsletters = new String[0];
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public void setNewsletters(String[] newsletters) {
        this.newsletters = newsletters;
    }

    public String[] getNewsletters() {
        return this.newsletters;
    }

    public SelectItem[] getNewsletterItems() {
        return newsletterItems;
    }

    public Date getShipDate() {
        return this.shipDate;
    }

    public void setShipDate(Date shipDate) {
        this.shipDate = shipDate;
    }

    public void setShippingOption(String shippingOption) {
        this.shippingOption = shippingOption;
    }

    public String getShippingOption() {
        return this.shippingOption;
    }

    public UIOutput getSpecialOfferText() {
        return this.specialOfferText;
    }

    public void setSpecialOfferText(UIOutput specialOfferText) {
        this.specialOfferText = specialOfferText;
    }

    public UISelectBoolean getSpecialOffer() {
        return this.specialOffer;
    }

    public void setSpecialOffer(UISelectBoolean specialOffer) {
        this.specialOffer = specialOffer;
    }

    public UIOutput getThankYou() {
        return this.thankYou;
    }

    public void setThankYou(UIOutput thankYou) {
        this.thankYou = thankYou;
    }

    public String getStringProperty() {
        return (this.stringProperty);
    }

    public void setStringProperty(String stringProperty) {
        this.stringProperty = stringProperty;
    }

    public String submit() {
        // Calculate and save the ship date
        int days = Integer.valueOf(shippingOption).intValue();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, days);
        setShipDate(cal.getTime());

        if ((cart.getTotal() > 100.00) && !specialOffer.isRendered()) {
            specialOfferText.setRendered(true);
            specialOffer.setRendered(true);

            return null;
        } else if (specialOffer.isRendered() && !thankYou.isRendered()) {
            thankYou.setRendered(true);

            return null;
        } else {
            try {
                bookRequestBean.updateInventory(cart);
            } catch (OrderException ex) {
                return "bookordererror";
            }

            cart.clear();

            return ("bookreceipt");
        }
    }
}

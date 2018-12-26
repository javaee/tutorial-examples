/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014-2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package javaeetutorial.dukesbookstore.web.managedbeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaeetutorial.dukesbookstore.entity.Book;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 * <p>Backing bean used by the
 * <code>/bookcatalog.xhtml</code>,
 * <code>/bookshowcart.xhtml</code>, and
 * <code>/bookcashier.xhtml</code> pages.</p>
 */
@Named("cart")
@SessionScoped
public class ShoppingCart extends AbstractBean implements Serializable {

    private static final Logger logger =
            Logger.getLogger("dukesbookstore.web.managedbeans.ShoppingCart");
    private static final long serialVersionUID = -115105724952554868L;
    HashMap<String, ShoppingCartItem> items = null;
    int numberOfItems = 0;

    public ShoppingCart() {
        items = new HashMap<>();
    }

    public synchronized void add(String bookId, Book book) {
        if (items.containsKey(bookId)) {
            ShoppingCartItem scitem = (ShoppingCartItem) items.get(bookId);
            scitem.incrementQuantity();
        } else {
            ShoppingCartItem newItem = new ShoppingCartItem(book);
            items.put(bookId, newItem);
        }
    }

    public synchronized void remove(String bookId) {
        if (items.containsKey(bookId)) {
            ShoppingCartItem scitem = (ShoppingCartItem) items.get(bookId);
            scitem.decrementQuantity();

            if (scitem.getQuantity() <= 0) {
                items.remove(bookId);
            }

            numberOfItems--;
        }
    }

    public synchronized List<ShoppingCartItem> getItems() {
        List<ShoppingCartItem> results = new ArrayList<>();
        results.addAll(this.items.values());

        return results;
    }

    public synchronized int getNumberOfItems() {
        numberOfItems = 0;
        for (ShoppingCartItem item : getItems()) {
            numberOfItems += item.getQuantity();
        }

        return numberOfItems;
    }

    public synchronized double getTotal() {
        double amount = 0.0;
        for (ShoppingCartItem item : getItems()) {
            Book bookDetails = (Book) item.getItem();

            amount += (item.getQuantity() * bookDetails.getPrice());
        }

        return roundOff(amount);
    }

    private double roundOff(double x) {
        long val = Math.round(x * 100); // cents

        return val / 100.0;
    }

    /**
     * <p>Buy the items currently in the shopping cart.</p>
     * @return the navigation page
     */
    public String buy() {
        // "Cannot happen" sanity check
        if (getNumberOfItems() < 1) {
            message(null, "CartEmpty");

            return (null);
        } else {
            return ("bookcashier");
        }
    }

    public synchronized void clear() {
        logger.log(Level.INFO, "Clearing cart.");
        items.clear();
        numberOfItems = 0;
        message(null, "CartCleared");
    }
}

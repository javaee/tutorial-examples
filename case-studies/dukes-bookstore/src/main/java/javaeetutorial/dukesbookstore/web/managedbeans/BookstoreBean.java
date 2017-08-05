/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.dukesbookstore.web.managedbeans;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaeetutorial.dukesbookstore.ejb.BookRequestBean;
import javaeetutorial.dukesbookstore.entity.Book;
import javaeetutorial.dukesbookstore.exception.BookNotFoundException;
import javaeetutorial.dukesbookstore.exception.BooksNotFoundException;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.FacesException;
import javax.inject.Named;

/**
 * <p>Backing bean for the <code>/bookstore.xhtml</code> page.</p>
 */
@Named("store")
@SessionScoped
public class BookstoreBean extends AbstractBean implements Serializable {

    private static final Logger logger =
            Logger.getLogger("dukesbookstore.web.managedBeans.BookStoreBean");
    private static final long serialVersionUID = 7829793160074383708L;
    private Book featured = null;
    protected String title;
    @EJB
    BookRequestBean bookRequestBean;

    /**
     * @return the <code>Book</code> for the featured book
     */
    public Book getFeatured() {
        int featuredBookPos = 4; // "The Green Project"
        if (featured == null) {
            try {
                featured = (Book) bookRequestBean.getBooks().get(featuredBookPos);
            } catch (BooksNotFoundException e) {
                // Real apps would deal with error conditions better than this
                throw new FacesException("Could not get books: " + e);
            }
        }

        return (featured);
    }

    /**
     * <p>Add the featured item to our shopping cart.</p>
     * @return the navigation page
     */
    public String add() {
        Book book = getFeatured();
        cart.add(book.getBookId(), book);
        message(null, "ConfirmAdd", new Object[]{book.getTitle()});

        return ("bookcatalog");
    }

    public String addSelected() {
        logger.log(Level.INFO, "Entering BookstoreBean.addSelected");
        try {
            String bookId = (String) context().getExternalContext().
                    getSessionMap().get("bookId");
            Book book = bookRequestBean.getBook(bookId);
            cart.add(bookId, book);
            message(null, "ConfirmAdd", new Object[]{book.getTitle()});
        } catch (BookNotFoundException e) {
            throw new FacesException("Could not get book: " + e);
        }
        return ("bookcatalog");
    }

    /**
     * <p>Show the details page for the featured book.</p>
     * @return the navigation page
     */
    public String details() {
        context().getExternalContext().getSessionMap().put(
                "selected",
                getFeatured());

        return ("bookdetails");
    }

    public String selectedDetails() {
        logger.log(Level.INFO, "Entering BookstoreBean.selectedDetails");
        try {
            String bookId = (String) context().getExternalContext().getSessionMap().get("bookId");
            Book book = bookRequestBean.getBook(bookId);
            context().getExternalContext().getSessionMap().put("selected", book);
        } catch (BookNotFoundException e) {
            throw new FacesException("Could not get book: " + e);
        }
        return ("bookdetails");
    }

    public String getSelectedTitle() {
        logger.log(Level.INFO, "Entering BookstoreBean.getSelectedTitle");
        try {
            String bookId = (String) context().getExternalContext().getSessionMap().get("bookId");
            Book book = bookRequestBean.getBook(bookId);
            title = book.getTitle();
        } catch (BookNotFoundException e) {
            throw new FacesException("Could not get book title: " + e);
        }
        return title;
    }

    public List<Book> getBooks() {
        try {
            return bookRequestBean.getBooks();
        } catch (BooksNotFoundException e) {
            throw new FacesException("Exception: " + e);
        }
    }
}

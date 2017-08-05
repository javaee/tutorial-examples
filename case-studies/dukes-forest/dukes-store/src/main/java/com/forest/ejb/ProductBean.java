/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package com.forest.ejb;

import com.forest.entity.Category;
import com.forest.entity.Product;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author markito
 */
@Stateless
public class ProductBean extends AbstractFacade<Product> {

    private static final Logger logger = 
            Logger.getLogger(ProductBean.class.getCanonicalName());
    
    @PersistenceContext(unitName="forestPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProductBean() {
        super(Product.class);
    }

    /**
     * Example usage of JPA CriteriaBuilder. You can also use NamedQueries
     * @param range
     * @param categoryId
     * @return 
     */
    public List<Product> findByCategory(int[] range, int categoryId) {       
         Category cat = new Category();
         cat.setId(categoryId);
         
         CriteriaBuilder qb = em.getCriteriaBuilder();
         CriteriaQuery<Product> query = qb.createQuery(Product.class);
         Root<Product> product = query.from(Product.class);
         query.where(qb.equal(product.get("category"), cat));

         List<Product> result = this.findRange(range, query);
         
         logger.log(Level.FINEST, "Product List size: {0}", result.size());
         
        return result;
    }
    
    
}

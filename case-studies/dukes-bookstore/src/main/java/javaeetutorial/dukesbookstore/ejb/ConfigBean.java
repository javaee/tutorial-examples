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

package javaeetutorial.dukesbookstore.ejb;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * <p>Singleton bean that initializes the book database for the bookstore
 * example.</p>
 */
@Singleton
@Startup
public class ConfigBean {

    @EJB
    private BookRequestBean request;

    @PostConstruct
    public void createData() {
        request.createBook("201", "Duke", "",
                "My Early Years: Growing Up on *7",
                30.75, false, 2005, "What a cool book.", 20);
        request.createBook("202", "Jeeves", "",
                "Web Servers for Fun and Profit", 40.75, true,
                2010, "What a cool book.", 20);
        request.createBook("203", "Masterson", "Webster",
                "Web Components for Web Developers",
                27.75, false, 2010, "What a cool book.", 20);
        request.createBook("205", "Novation", "Kevin",
                "From Oak to Java: The Revolution of a Language",
                10.75, true, 2008, "What a cool book.", 20);
        request.createBook("206", "Thrilled", "Ben",
                "The Green Project: Programming for Consumer Devices",
                30.00, true, 2008, "What a cool book.", 20);
        request.createBook("207", "Coding", "Happy",
                "Java Intermediate Bytecodes", 30.95, true,
                2010, "What a cool book.", 20);

    }
}

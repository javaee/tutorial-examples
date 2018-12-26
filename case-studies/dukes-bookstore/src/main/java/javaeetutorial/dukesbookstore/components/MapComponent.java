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

package javaeetutorial.dukesbookstore.components;

import javaeetutorial.dukesbookstore.listeners.AreaSelectedEvent;
import javax.faces.component.FacesComponent;
import javax.faces.component.UICommand;

/**
 * <p>{@link MapComponent} is a JavaServer Faces component that corresponds to a
 * client-side image map. It can have one or more children of type
 * {@link AreaComponent}, each representing hot spots, which a user can click on
 * and mouse over.</p>
 *
 * <p>This component is a source of {@link AreaSelectedEvent} events, which are
 * fired whenever the current area is changed.</p>
 *
 * <p>Use of the javax.faces.component.StateHelper interface allows the use of
 * expressions and also makes it unnecessary to implement saveState() and
 * restoreState().</p>
 */
@FacesComponent("DemoMap")
public class MapComponent extends UICommand {

    private enum PropertyKeys {
        current;
    }
    private final String current = null;

    public MapComponent() {
        super();
    }

    /**
     * @return the alternate text label for the currently selected child
     * {@link AreaComponent}
     */
    public String getCurrent() {
        return (String) getStateHelper().eval(PropertyKeys.current, null);
    }

    /**
     * <p>Set the alternate text label for the currently selected child. If this
     * is different from the previous value, fire an {@link AreaSelectedEvent}
     * to interested listeners.</p>
     *
     * @param current The new alternate text label
     */
    public void setCurrent(String current) {
        if (this.getParent() == null) {
            return;
        }

        String previous = (String) getStateHelper().get(current);
        getStateHelper().put(PropertyKeys.current, current);

        // Fire an {@link AreaSelectedEvent} if appropriate
        if ((previous == null) && (current == null)) {
            // do nothing
        } else if ((previous != null)
                && (current != null)
                && (previous.equals(current))) {
            // do nothing
        } else {
            this.queueEvent(new AreaSelectedEvent(this));
        }
    }

    /**
     * @return the component family for this component
     */
    @Override
    public String getFamily() {
        return ("Map");
    }
}

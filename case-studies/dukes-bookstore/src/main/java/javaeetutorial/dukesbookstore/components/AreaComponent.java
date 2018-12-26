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

import javaeetutorial.dukesbookstore.model.ImageArea;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;

/**
 * <p>{@link AreaComponent} is a JavaServer Faces component that represents a
 * particular hotspot in a client-side image map defined by our parent
 * {@link MapComponent}. The
 * <code>valueRef</code> property (if present) must point at a JavaBean of type
 * <code>components.model.ImageArea</code>; if not present, an
 * <code>ImageArea</code> instance will be synthesized from the values of the
 * <code>alt</code>,
 * <code>coords</code>, and
 * <code>shape</code> properties, and assigned to the
 * <code>value</code> property.</p>
 *
 * <p>Use of the javax.faces.component.StateHelper interface allows the use of
 * expressions and also makes it unnecessary to implement saveState() and
 * restoreState().</p>
 */
@FacesComponent("DemoArea")
public class AreaComponent extends UIOutput {

    private enum PropertyKeys {
        alt, coords, shape, targetImage;
    }

    /**
     * @return the alternate text for our synthesized {@link ImageArea}
     */
    public String getAlt() {
        return (String) getStateHelper().eval(PropertyKeys.alt, null);
    }

    /**
     * <p>Set the alternate text for our synthesized {@link ImageArea}.</p>
     *
     * @param alt The new alternate text
     */
    public void setAlt(String alt) {
        getStateHelper().put(PropertyKeys.alt, alt);
    }

    /**
     * @return the hotspot coordinates for our synthesized {@link ImageArea}
     */
    public String getCoords() {
        return (String) getStateHelper().eval(PropertyKeys.coords, null);
    }

    /**
     * <p>Set the hotspot coordinates for our synthesized {@link ImageArea}.</p>
     *
     * @param coords The new coordinates
     */
    public void setCoords(String coords) {
        getStateHelper().put(PropertyKeys.coords, coords);
    }

    /**
     * @return the shape for our synthesized {@link ImageArea}
     */
    public String getShape() {
        return (String) getStateHelper().eval(PropertyKeys.shape, null);
    }

    /**
     * <p>Set the shape for our synthesized {@link ImageArea}.</p>
     *
     * @param shape The new shape (default, rect, circle, poly)
     */
    public void setShape(String shape) {
        getStateHelper().put(PropertyKeys.shape, shape);
    }

    /**
     * <p>Return the image that is the target of this
     * <code>AreaComponent</code>.</p>
     *
     * @return the target image of this AreaComponent
     */
    public String getTargetImage() {
        return (String) getStateHelper().eval(PropertyKeys.targetImage, null);
    }

    /**
     * <p>Set the image that is the target of this
     * <code>AreaComponent</code>.</p>
     *
     * @param targetImage the ID of the target of this
     * <code>AreaComponent</code>
     */
    public void setTargetImage(String targetImage) {
        getStateHelper().put(PropertyKeys.targetImage, targetImage);
    }

    /**
     * @return the component family for this component
     */
    @Override
    public String getFamily() {
        return ("Area");
    }

    // UIOutput Methods
    /**
     * <p>Synthesize and return an {@link ImageArea} bean for this hotspot, if
     * there is no
     * <code>valueRef</code> property on this component.</p>
     */
    @Override
    public Object getValue() {
        if (super.getValue() == null) {
            setValue(new ImageArea(getAlt(), getCoords(), getShape()));
        }

        return (super.getValue());
    }
}

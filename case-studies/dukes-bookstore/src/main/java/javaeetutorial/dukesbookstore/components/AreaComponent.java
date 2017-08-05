/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
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

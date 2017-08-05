/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.dukesbookstore.listeners;

import javaeetutorial.dukesbookstore.components.MapComponent;
import javax.faces.event.ActionEvent;

/**
 * <p>An {@link ActionEvent} indicating that the specified {@link AreaComponent}
 * has just become the currently selected hotspot within the source
 * {@link MapComponent}.</p>
 */
public class AreaSelectedEvent extends ActionEvent {

    private static final long serialVersionUID = 5030325260451465110L;

    /**
     * <p>Construct a new {@link AreaSelectedEvent} from the specified source
     * map.</p>
     *
     * @param map The {@link MapComponent} originating this event
     */
    public AreaSelectedEvent(MapComponent map) {
        super(map);
    }

    /**
     * @return the {@link MapComponent} of the map for which an area was
     * selected.
     */
    public MapComponent getMapComponent() {
        return ((MapComponent) getComponent());
    }
}

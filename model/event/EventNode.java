/** @author María Fortuna */

package com.rpgmaker.model.event;

import java.io.Serializable;
import java.util.UUID;

/**
 * Clase base para todos los nodos en el grafo de eventos.
 */
public abstract class EventNode implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String id;
    protected int x;
    protected int y;

    public EventNode() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public abstract String getNodeType();
}

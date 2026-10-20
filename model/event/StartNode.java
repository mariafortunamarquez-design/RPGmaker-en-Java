/** @author María Fortuna */

package com.rpgmaker.model.event;

/**
 * Nodo de inicio del evento.
 */
public class StartNode extends EventNode {
    private static final long serialVersionUID = 1L;

    private String nextNodeId;

    public StartNode() {
        super();
    }

    public String getNextNodeId() {
        return nextNodeId;
    }

    public void setNextNodeId(String nextNodeId) {
        this.nextNodeId = nextNodeId;
    }

    @Override
    public String getNodeType() {
        return "Start";
    }
}

/** @author María Fortuna */

package com.rpgmaker.model.event;

/**
 * Nodo de diálogo.
 */
public class DialogNode extends EventNode {
    private static final long serialVersionUID = 1L;

    private String text;
    private String nextNodeId;

    public DialogNode() {
        super();
        this.text = "";
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNextNodeId() {
        return nextNodeId;
    }

    public void setNextNodeId(String nextNodeId) {
        this.nextNodeId = nextNodeId;
    }

    @Override
    public String getNodeType() {
        return "Dialog";
    }
}

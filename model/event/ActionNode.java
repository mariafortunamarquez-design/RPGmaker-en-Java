/** @author María Fortuna */

package com.rpgmaker.model.event;

import com.rpgmaker.model.ActionType;
import java.util.HashMap;
import java.util.Map;

/**
 * Nodo para ejecutar una acción específica (dar oro, teleport, etc.).
 */
public class ActionNode extends EventNode {
    private static final long serialVersionUID = 1L;

    private ActionType actionType;
    private Map<String, String> parameters;
    private String nextNodeId;

    public ActionNode() {
        super();
        this.actionType = ActionType.ESTABLECER_FLAG;
        this.parameters = new HashMap<>();
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public void setParameter(String key, String value) {
        this.parameters.put(key, value);
    }

    public String getParameter(String key) {
        return this.parameters.getOrDefault(key, "");
    }

    public String getNextNodeId() {
        return nextNodeId;
    }

    public void setNextNodeId(String nextNodeId) {
        this.nextNodeId = nextNodeId;
    }

    @Override
    public String getNodeType() {
        return "Action";
    }
}

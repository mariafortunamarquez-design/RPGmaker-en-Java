/** @author María Fortuna */

package com.rpgmaker.model.event;

/**
 * Nodo de condición (Flag o Item).
 */
public class ConditionNode extends EventNode {
    private static final long serialVersionUID = 1L;

    public enum ConditionType {
        FLAG, ITEM
    }

    private ConditionType conditionType;
    private String targetId; // ID del flag o item
    private boolean expectedValue; // Para flags: true/false. Para items: true (tenerlo)

    private String trueNextNodeId;
    private String falseNextNodeId;

    public ConditionNode() {
        super();
        this.conditionType = ConditionType.FLAG;
        this.targetId = "";
        this.expectedValue = true;
    }

    public ConditionType getConditionType() {
        return conditionType;
    }

    public void setConditionType(ConditionType conditionType) {
        this.conditionType = conditionType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public boolean isExpectedValue() {
        return expectedValue;
    }

    public void setExpectedValue(boolean expectedValue) {
        this.expectedValue = expectedValue;
    }

    public String getTrueNextNodeId() {
        return trueNextNodeId;
    }

    public void setTrueNextNodeId(String trueNextNodeId) {
        this.trueNextNodeId = trueNextNodeId;
    }

    public String getFalseNextNodeId() {
        return falseNextNodeId;
    }

    public void setFalseNextNodeId(String falseNextNodeId) {
        this.falseNextNodeId = falseNextNodeId;
    }

    @Override
    public String getNodeType() {
        return "Condition";
    }
}

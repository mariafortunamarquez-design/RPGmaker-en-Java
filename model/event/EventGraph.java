/** @author María Fortuna */

package com.rpgmaker.model.event;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Un grafo de nodos de evento. Reemplaza la antigua lista lineal de acciones.
 */
public class EventGraph implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, EventNode> nodes;
    private String startNodeId;

    public EventGraph() {
        this.nodes = new HashMap<>();
        // Crear un nodo de inicio por defecto
        StartNode startNode = new StartNode();
        startNode.setX(50);
        startNode.setY(150);
        this.startNodeId = startNode.getId();
        this.nodes.put(startNode.getId(), startNode);
    }

    public Map<String, EventNode> getNodes() {
        return nodes;
    }

    public void setNodes(Map<String, EventNode> nodes) {
        this.nodes = nodes;
    }

    public String getStartNodeId() {
        return startNodeId;
    }

    public void setStartNodeId(String startNodeId) {
        this.startNodeId = startNodeId;
    }

    public void addNode(EventNode node) {
        this.nodes.put(node.getId(), node);
    }

    public void removeNode(String id) {
        // No se puede eliminar el nodo inicial
        if (id.equals(startNodeId)) {
            return;
        }
        this.nodes.remove(id);
        // Limpiar conexiones huérfanas
        for (EventNode node : nodes.values()) {
            if (node instanceof StartNode) {
                if (((StartNode) node).getNextNodeId() != null && ((StartNode) node).getNextNodeId().equals(id)) {
                    ((StartNode) node).setNextNodeId(null);
                }
            } else if (node instanceof DialogNode) {
                if (((DialogNode) node).getNextNodeId() != null && ((DialogNode) node).getNextNodeId().equals(id)) {
                    ((DialogNode) node).setNextNodeId(null);
                }
            } else if (node instanceof ActionNode) {
                if (((ActionNode) node).getNextNodeId() != null && ((ActionNode) node).getNextNodeId().equals(id)) {
                    ((ActionNode) node).setNextNodeId(null);
                }
            } else if (node instanceof ConditionNode) {
                if (((ConditionNode) node).getTrueNextNodeId() != null
                        && ((ConditionNode) node).getTrueNextNodeId().equals(id)) {
                    ((ConditionNode) node).setTrueNextNodeId(null);
                }
                if (((ConditionNode) node).getFalseNextNodeId() != null
                        && ((ConditionNode) node).getFalseNextNodeId().equals(id)) {
                    ((ConditionNode) node).setFalseNextNodeId(null);
                }
            }
        }
    }

    public EventNode getNode(String id) {
        if (id == null)
            return null;
        return nodes.get(id);
    }
}

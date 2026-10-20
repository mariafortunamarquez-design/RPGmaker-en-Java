/** @author María Fortuna */

package com.rpgmaker.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Una acción individual dentro de un evento. Usa un mapa de parámetros flexible
 * para que diferentes tipos de acciones puedan llevar datos distintos.
 */
public class Action {
    private ActionType type;
    private Map<String, String> parameters;

    public Action() {
        this.type = ActionType.MOSTRAR_DIALOGO;
        this.parameters = new HashMap<>();
    }

    public Action(ActionType type) {
        this();
        this.type = type;
    }

    public ActionType getType() {
        return type;
    }

    public void setType(ActionType type) {
        this.type = type;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public void setParameter(String key, String value) {
        parameters.put(key, value);
    }

    public String getParameter(String key) {
        return parameters.getOrDefault(key, "");
    }

    @Override
    public String toString() {
        return type.getDisplayName();
    }
}

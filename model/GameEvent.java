/** @author María Fortuna */

package com.rpgmaker.model;

import java.util.UUID;

/**
 * Representa un evento del juego que puede ser activado por la interacción del
 * jugador.
 */
public class GameEvent {

    public enum TriggerType {
        AL_INTERACTUAR("Al Interactuar"),
        AL_PISAR("Al Pisar"),
        AL_ENTRAR_MAPA("Al Entrar al Mapa"),
        EJECUCION_AUTO("Ejecución Automática");

        private final String nombreVisible;

        TriggerType(String nombreVisible) {
            this.nombreVisible = nombreVisible;
        }

        public String getDisplayName() {
            return nombreVisible;
        }
    }

    private String id;
    private String name;
    private TriggerType triggerType;
    private com.rpgmaker.model.event.EventGraph eventGraph;

    public GameEvent() {
        this.id = UUID.randomUUID().toString();
        this.name = "Nuevo Evento";
        this.triggerType = TriggerType.AL_INTERACTUAR;
        this.eventGraph = new com.rpgmaker.model.event.EventGraph();
    }

    public GameEvent(String name, TriggerType triggerType) {
        this();
        this.name = name;
        this.triggerType = triggerType;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(TriggerType triggerType) {
        this.triggerType = triggerType;
    }

    public com.rpgmaker.model.event.EventGraph getEventGraph() {
        return eventGraph;
    }

    public void setEventGraph(com.rpgmaker.model.event.EventGraph eventGraph) {
        this.eventGraph = eventGraph;
    }

    @Override
    public String toString() {
        return name + " [" + triggerType.getDisplayName() + "]";
    }
}

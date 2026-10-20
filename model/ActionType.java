/** @author María Fortuna */

package com.rpgmaker.model;

/**
 * Tipos de acciones que pueden ser activadas por eventos.
 */
public enum ActionType {
    MOSTRAR_DIALOGO("Mostrar Diálogo"),
    DAR_OBJETO("Dar Objeto"),
    QUITAR_OBJETO("Quitar Objeto"),
    TELETRANSPORTAR("Teletransportar Jugador"),
    INICIAR_BATALLA("Iniciar Batalla"),
    ESTABLECER_FLAG("Establecer Flag"),
    VERIFICAR_FLAG("Verificar Flag"),
    CURAR_JUGADOR("Curar Jugador"),
    DAÑAR_JUGADOR("Dañar Jugador"),
    DAR_ORO("Dar Oro"),
    QUITAR_ORO("Quitar Oro"),
    DAR_EXP("Dar Experiencia"),
    REPRODUCIR_SONIDO("Reproducir Sonido"),
    CAMBIAR_MAPA("Cambiar Mapa"),
    MOSTRAR_OPCION("Mostrar Opción"),
    FIN_DEL_JUEGO("Fin del Juego"),
    ABRIR_TIENDA("Abrir Tienda");

    private final String nombreVisible;

    ActionType(String nombreVisible) {
        this.nombreVisible = nombreVisible;
    }

    public String getDisplayName() {
        return nombreVisible;
    }
}

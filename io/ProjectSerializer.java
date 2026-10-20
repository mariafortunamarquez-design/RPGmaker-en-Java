/** @author María Fortuna */

package com.rpgmaker.io;

import com.rpgmaker.model.*;

import java.io.*;
import java.util.*;

/**
 * Maneja el guardado/carga de GameProject a/desde archivos de propiedades
 * simples (.rpg).
 * Usa serialización manual con Properties de Java para evitar dependencias
 * externas.
 */
public class ProjectSerializer {

    /**
     * Guardar un proyecto a un archivo.
     */
    public static void save(GameProject project, File file) throws IOException {
        Properties props = new Properties();

        // Datos del proyecto
        props.setProperty("project.name", project.getName());
        props.setProperty("project.author", project.getAuthor());
        props.setProperty("project.description", project.getDescription());
        props.setProperty("project.version", project.getVersion());

        // Configuración del jugador
        PlayerConfig pc = project.getPlayerConfig();
        props.setProperty("player.name", pc.getName());
        props.setProperty("player.maxHp", String.valueOf(pc.getMaxHp()));
        props.setProperty("player.attack", String.valueOf(pc.getAttack()));
        props.setProperty("player.defense", String.valueOf(pc.getDefense()));
        props.setProperty("player.speed", String.valueOf(pc.getSpeed()));
        props.setProperty("player.startingGold", String.valueOf(pc.getStartingGold()));
        props.setProperty("player.startingLevel", String.valueOf(pc.getStartingLevel()));
        props.setProperty("player.startingMapId", pc.getStartingMapId() != null ? pc.getStartingMapId() : "");
        props.setProperty("player.startX", String.valueOf(pc.getStartX()));
        props.setProperty("player.startY", String.valueOf(pc.getStartY()));

        // Mapas
        props.setProperty("maps.count", String.valueOf(project.getMaps().size()));
        for (int i = 0; i < project.getMaps().size(); i++) {
            GameMap map = project.getMaps().get(i);
            String prefix = "map." + i + ".";
            props.setProperty(prefix + "id", map.getId());
            props.setProperty(prefix + "name", map.getName());
            props.setProperty(prefix + "width", String.valueOf(map.getWidth()));
            props.setProperty(prefix + "height", String.valueOf(map.getHeight()));
            props.setProperty(prefix + "encounterRate", String.valueOf(map.getEncounterRate()));

            // Casillas
            StringBuilder tilesStr = new StringBuilder();
            for (int y = 0; y < map.getHeight(); y++) {
                for (int x = 0; x < map.getWidth(); x++) {
                    Tile tile = map.getTile(x, y);
                    if (x > 0 || y > 0)
                        tilesStr.append(",");
                    tilesStr.append(tile.getType().ordinal());
                    if (!tile.isWalkable() && tile.getType().isDefaultWalkable()) {
                        tilesStr.append("!"); // marca no-transitable forzado
                    }
                }
            }
            props.setProperty(prefix + "tiles", tilesStr.toString());

            // Entidades
            props.setProperty(prefix + "entities.count", String.valueOf(map.getEntities().size()));
            for (int j = 0; j < map.getEntities().size(); j++) {
                Entity e = map.getEntities().get(j);
                String ep = prefix + "entity." + j + ".";
                props.setProperty(ep + "id", e.getId());
                props.setProperty(ep + "name", e.getName());
                props.setProperty(ep + "type", e.getType().name());
                props.setProperty(ep + "x", String.valueOf(e.getX()));
                props.setProperty(ep + "y", String.valueOf(e.getY()));
                props.setProperty(ep + "maxHp", String.valueOf(e.getMaxHp()));
                props.setProperty(ep + "attack", String.valueOf(e.getAttack()));
                props.setProperty(ep + "defense", String.valueOf(e.getDefense()));
                props.setProperty(ep + "speed", String.valueOf(e.getSpeed()));
                props.setProperty(ep + "expReward", String.valueOf(e.getExpReward()));
                props.setProperty(ep + "goldReward", String.valueOf(e.getGoldReward()));
                props.setProperty(ep + "dialogue", String.join("|", e.getDialogueLines()));
            }
        }

        // Objetos
        props.setProperty("items.count", String.valueOf(project.getItems().size()));
        for (int i = 0; i < project.getItems().size(); i++) {
            Item item = project.getItems().get(i);
            String prefix = "item." + i + ".";
            props.setProperty(prefix + "id", item.getId());
            props.setProperty(prefix + "name", item.getName());
            props.setProperty(prefix + "description", item.getDescription());
            props.setProperty(prefix + "type", item.getType().name());
            props.setProperty(prefix + "attackBonus", String.valueOf(item.getAttackBonus()));
            props.setProperty(prefix + "defenseBonus", String.valueOf(item.getDefenseBonus()));
            props.setProperty(prefix + "hpBonus", String.valueOf(item.getHpBonus()));
            props.setProperty(prefix + "speedBonus", String.valueOf(item.getSpeedBonus()));
            props.setProperty(prefix + "healAmount", String.valueOf(item.getHealAmount()));
            props.setProperty(prefix + "buyPrice", String.valueOf(item.getBuyPrice()));
            props.setProperty(prefix + "sellPrice", String.valueOf(item.getSellPrice()));
            props.setProperty(prefix + "stackable", String.valueOf(item.isStackable()));
        }

        // Eventos
        props.setProperty("events.count", String.valueOf(project.getEvents().size()));
        for (int i = 0; i < project.getEvents().size(); i++) {
            GameEvent event = project.getEvents().get(i);
            String prefix = "event." + i + ".";
            props.setProperty(prefix + "id", event.getId());
            props.setProperty(prefix + "name", event.getName());
            props.setProperty(prefix + "triggerType", event.getTriggerType().name());

            com.rpgmaker.model.event.EventGraph graph = event.getEventGraph();
            props.setProperty(prefix + "startNodeId", graph.getStartNodeId());

            int j = 0;
            props.setProperty(prefix + "nodes.count", String.valueOf(graph.getNodes().size()));
            for (com.rpgmaker.model.event.EventNode node : graph.getNodes().values()) {
                String np = prefix + "node." + j + ".";
                props.setProperty(np + "id", node.getId());
                props.setProperty(np + "type", node.getNodeType());
                props.setProperty(np + "x", String.valueOf(node.getX()));
                props.setProperty(np + "y", String.valueOf(node.getY()));

                if (node instanceof com.rpgmaker.model.event.StartNode sn) {
                    props.setProperty(np + "next", sn.getNextNodeId() != null ? sn.getNextNodeId() : "");
                } else if (node instanceof com.rpgmaker.model.event.DialogNode dn) {
                    props.setProperty(np + "text", dn.getText());
                    props.setProperty(np + "next", dn.getNextNodeId() != null ? dn.getNextNodeId() : "");
                } else if (node instanceof com.rpgmaker.model.event.ConditionNode cn) {
                    props.setProperty(np + "condType", cn.getConditionType().name());
                    props.setProperty(np + "targetId", cn.getTargetId());
                    props.setProperty(np + "expected", String.valueOf(cn.isExpectedValue()));
                    props.setProperty(np + "trueNext", cn.getTrueNextNodeId() != null ? cn.getTrueNextNodeId() : "");
                    props.setProperty(np + "falseNext", cn.getFalseNextNodeId() != null ? cn.getFalseNextNodeId() : "");
                } else if (node instanceof com.rpgmaker.model.event.ActionNode an) {
                    props.setProperty(np + "actionType", an.getActionType().name());
                    props.setProperty(np + "next", an.getNextNodeId() != null ? an.getNextNodeId() : "");

                    StringBuilder params = new StringBuilder();
                    for (Map.Entry<String, String> entry : an.getParameters().entrySet()) {
                        if (params.length() > 0)
                            params.append("||");
                        params.append(entry.getKey()).append("=").append(entry.getValue());
                    }
                    props.setProperty(np + "params", params.toString());
                }
                j++;
            }
        }

        try (OutputStream out = new FileOutputStream(file)) {
            props.store(out, "Proyecto RPG Maker");
        }
        project.setFilePath(file.getAbsolutePath());
    }

    /**
     * Cargar un proyecto desde un archivo.
     */
    public static GameProject load(File file) throws IOException {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(file)) {
            props.load(in);
        }

        GameProject project = new GameProject();
        project.setName(props.getProperty("project.name", "Mi Juego RPG"));
        project.setAuthor(props.getProperty("project.author", ""));
        project.setDescription(props.getProperty("project.description", ""));
        project.setVersion(props.getProperty("project.version", "1.0"));

        // Jugador
        PlayerConfig pc = project.getPlayerConfig();
        pc.setName(props.getProperty("player.name", "Héroe"));
        pc.setMaxHp(Integer.parseInt(props.getProperty("player.maxHp", "100")));
        pc.setAttack(Integer.parseInt(props.getProperty("player.attack", "10")));
        pc.setDefense(Integer.parseInt(props.getProperty("player.defense", "5")));
        pc.setSpeed(Integer.parseInt(props.getProperty("player.speed", "8")));
        pc.setStartingGold(Integer.parseInt(props.getProperty("player.startingGold", "50")));
        pc.setStartingLevel(Integer.parseInt(props.getProperty("player.startingLevel", "1")));
        String startMapId = props.getProperty("player.startingMapId", "");
        pc.setStartingMapId(startMapId.isEmpty() ? null : startMapId);
        pc.setStartX(Integer.parseInt(props.getProperty("player.startX", "1")));
        pc.setStartY(Integer.parseInt(props.getProperty("player.startY", "1")));

        // Mapas
        int mapCount = Integer.parseInt(props.getProperty("maps.count", "0"));
        project.getMaps().clear();
        for (int i = 0; i < mapCount; i++) {
            String prefix = "map." + i + ".";
            int w = Integer.parseInt(props.getProperty(prefix + "width", "20"));
            int h = Integer.parseInt(props.getProperty(prefix + "height", "15"));
            GameMap map = new GameMap(props.getProperty(prefix + "name", "Mapa"), w, h);
            map.setId(props.getProperty(prefix + "id", map.getId()));
            map.setEncounterRate(Integer.parseInt(props.getProperty(prefix + "encounterRate", "0")));

            // Casillas
            String tilesStr = props.getProperty(prefix + "tiles", "");
            if (!tilesStr.isEmpty()) {
                String[] tileTokens = tilesStr.split(",");
                TileType[] tileTypes = TileType.values();
                int idx = 0;
                for (int y = 0; y < h && idx < tileTokens.length; y++) {
                    for (int x = 0; x < w && idx < tileTokens.length; x++) {
                        String token = tileTokens[idx++];
                        boolean forceBlock = token.endsWith("!");
                        if (forceBlock)
                            token = token.substring(0, token.length() - 1);
                        int ord = Integer.parseInt(token);
                        if (ord >= 0 && ord < tileTypes.length) {
                            map.setTile(x, y, tileTypes[ord]);
                            if (forceBlock) {
                                Tile t = map.getTile(x, y);
                                if (t != null)
                                    t.setWalkable(false);
                            }
                        }
                    }
                }
            }

            // Entidades
            int entityCount = Integer.parseInt(props.getProperty(prefix + "entities.count", "0"));
            for (int j = 0; j < entityCount; j++) {
                String ep = prefix + "entity." + j + ".";
                Entity e = new Entity();
                e.setId(props.getProperty(ep + "id", e.getId()));
                e.setName(props.getProperty(ep + "name", "Entidad"));
                try {
                    e.setType(EntityType.valueOf(props.getProperty(ep + "type", "NPC")));
                } catch (Exception ex) {
                    e.setType(EntityType.NPC);
                }
                e.setX(Integer.parseInt(props.getProperty(ep + "x", "0")));
                e.setY(Integer.parseInt(props.getProperty(ep + "y", "0")));
                e.setMaxHp(Integer.parseInt(props.getProperty(ep + "maxHp", "10")));
                e.setHp(e.getMaxHp());
                e.setAttack(Integer.parseInt(props.getProperty(ep + "attack", "3")));
                e.setDefense(Integer.parseInt(props.getProperty(ep + "defense", "2")));
                e.setSpeed(Integer.parseInt(props.getProperty(ep + "speed", "5")));
                e.setExpReward(Integer.parseInt(props.getProperty(ep + "expReward", "5")));
                e.setGoldReward(Integer.parseInt(props.getProperty(ep + "goldReward", "3")));
                String dialogue = props.getProperty(ep + "dialogue", "");
                if (!dialogue.isEmpty()) {
                    e.setDialogueLines(new ArrayList<>(Arrays.asList(dialogue.split("\\|"))));
                }
                map.addEntity(e);
            }

            project.addMap(map);
        }

        // Objetos
        int itemCount = Integer.parseInt(props.getProperty("items.count", "0"));
        project.getItems().clear();
        for (int i = 0; i < itemCount; i++) {
            String prefix = "item." + i + ".";
            Item item = new Item();
            item.setId(props.getProperty(prefix + "id", item.getId()));
            item.setName(props.getProperty(prefix + "name", "Objeto"));
            item.setDescription(props.getProperty(prefix + "description", ""));
            try {
                item.setType(ItemType.valueOf(props.getProperty(prefix + "type", "CONSUMIBLE")));
            } catch (Exception ex) {
                item.setType(ItemType.CONSUMIBLE);
            }
            item.setAttackBonus(Integer.parseInt(props.getProperty(prefix + "attackBonus", "0")));
            item.setDefenseBonus(Integer.parseInt(props.getProperty(prefix + "defenseBonus", "0")));
            item.setHpBonus(Integer.parseInt(props.getProperty(prefix + "hpBonus", "0")));
            item.setSpeedBonus(Integer.parseInt(props.getProperty(prefix + "speedBonus", "0")));
            item.setHealAmount(Integer.parseInt(props.getProperty(prefix + "healAmount", "0")));
            item.setBuyPrice(Integer.parseInt(props.getProperty(prefix + "buyPrice", "10")));
            item.setSellPrice(Integer.parseInt(props.getProperty(prefix + "sellPrice", "5")));
            item.setStackable(Boolean.parseBoolean(props.getProperty(prefix + "stackable", "true")));
            project.addItem(item);
        }

        // Eventos
        int eventCount = Integer.parseInt(props.getProperty("events.count", "0"));
        project.getEvents().clear();
        for (int i = 0; i < eventCount; i++) {
            String prefix = "event." + i + ".";
            GameEvent event = new GameEvent();
            event.setId(props.getProperty(prefix + "id", event.getId()));
            event.setName(props.getProperty(prefix + "name", "Evento"));
            try {
                event.setTriggerType(
                        GameEvent.TriggerType.valueOf(props.getProperty(prefix + "triggerType", "AL_INTERACTUAR")));
            } catch (Exception ex) {
                event.setTriggerType(GameEvent.TriggerType.AL_INTERACTUAR);
            }

            com.rpgmaker.model.event.EventGraph graph = new com.rpgmaker.model.event.EventGraph();
            graph.getNodes().clear(); // Limpiar el de por defecto
            graph.setStartNodeId(props.getProperty(prefix + "startNodeId", ""));

            int nodeCount = Integer.parseInt(props.getProperty(prefix + "nodes.count", "0"));
            for (int j = 0; j < nodeCount; j++) {
                String np = prefix + "node." + j + ".";
                String type = props.getProperty(np + "type", "");
                String id = props.getProperty(np + "id", "");
                int x = Integer.parseInt(props.getProperty(np + "x", "50"));
                int y = Integer.parseInt(props.getProperty(np + "y", "50"));

                com.rpgmaker.model.event.EventNode node = null;

                if ("Start".equals(type)) {
                    node = new com.rpgmaker.model.event.StartNode();
                    String next = props.getProperty(np + "next", "");
                    ((com.rpgmaker.model.event.StartNode) node).setNextNodeId(next.isEmpty() ? null : next);
                } else if ("Dialog".equals(type)) {
                    node = new com.rpgmaker.model.event.DialogNode();
                    ((com.rpgmaker.model.event.DialogNode) node).setText(props.getProperty(np + "text", ""));
                    String next = props.getProperty(np + "next", "");
                    ((com.rpgmaker.model.event.DialogNode) node).setNextNodeId(next.isEmpty() ? null : next);
                } else if ("Condition".equals(type)) {
                    node = new com.rpgmaker.model.event.ConditionNode();
                    try {
                        ((com.rpgmaker.model.event.ConditionNode) node)
                                .setConditionType(com.rpgmaker.model.event.ConditionNode.ConditionType
                                        .valueOf(props.getProperty(np + "condType", "FLAG")));
                    } catch (Exception e) {
                    }
                    ((com.rpgmaker.model.event.ConditionNode) node).setTargetId(props.getProperty(np + "targetId", ""));
                    ((com.rpgmaker.model.event.ConditionNode) node)
                            .setExpectedValue(Boolean.parseBoolean(props.getProperty(np + "expected", "true")));

                    String trueNext = props.getProperty(np + "trueNext", "");
                    ((com.rpgmaker.model.event.ConditionNode) node)
                            .setTrueNextNodeId(trueNext.isEmpty() ? null : trueNext);
                    String falseNext = props.getProperty(np + "falseNext", "");
                    ((com.rpgmaker.model.event.ConditionNode) node)
                            .setFalseNextNodeId(falseNext.isEmpty() ? null : falseNext);
                } else if ("Action".equals(type)) {
                    node = new com.rpgmaker.model.event.ActionNode();
                    try {
                        ((com.rpgmaker.model.event.ActionNode) node).setActionType(com.rpgmaker.model.ActionType
                                .valueOf(props.getProperty(np + "actionType", "ESTABLECER_FLAG")));
                    } catch (Exception e) {
                    }
                    String next = props.getProperty(np + "next", "");
                    ((com.rpgmaker.model.event.ActionNode) node).setNextNodeId(next.isEmpty() ? null : next);

                    String paramsStr = props.getProperty(np + "params", "");
                    if (!paramsStr.isEmpty()) {
                        for (String pair : paramsStr.split("\\|\\|")) {
                            int eq = pair.indexOf('=');
                            if (eq > 0) {
                                ((com.rpgmaker.model.event.ActionNode) node).setParameter(pair.substring(0, eq),
                                        pair.substring(eq + 1));
                            }
                        }
                    }
                }

                if (node != null) {
                    node.setId(id);
                    node.setX(x);
                    node.setY(y);
                    graph.addNode(node);
                }
            }

            // Si el grafo de un proyecto antiguo (o sin eventos bien importados) está
            // vacío, le ponemos un StartNode
            if (graph.getNodes().isEmpty()) {
                graph = new com.rpgmaker.model.event.EventGraph();
            }

            event.setEventGraph(graph);
            project.addEvent(event);
        }

        project.setFilePath(file.getAbsolutePath());
        return project;
    }
}

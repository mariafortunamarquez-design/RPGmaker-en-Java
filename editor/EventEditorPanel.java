/** @author María Fortuna */

package com.rpgmaker.editor;

import com.rpgmaker.editor.node.GraphViewPanel;
import com.rpgmaker.editor.node.NodeInspectorPanel;
import com.rpgmaker.model.GameEvent;

import javax.swing.*;
import java.awt.*;

/**
 * Panel para crear y editar eventos del juego usando el sistema visual de
 * nodos.
 */
public class EventEditorPanel extends JPanel {

    private MainFrame mainFrame;
    private DefaultListModel<GameEvent> eventListModel;
    private JList<GameEvent> eventList;

    private JTextField nameField, idField;
    private JComboBox<GameEvent.TriggerType> triggerCombo;

    private GraphViewPanel graphViewPanel;
    private NodeInspectorPanel nodeInspectorPanel;

    private boolean updating = false;

    public EventEditorPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(4, 4));
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        eventListModel = new DefaultListModel<>();
        eventList = new JList<>(eventListModel);
        eventList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                showEventDetails(eventList.getSelectedValue());
        });

        JScrollPane eventScroll = new JScrollPane(eventList);
        eventScroll.setBorder(BorderFactory.createTitledBorder("Eventos"));

        JPanel eventBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        JButton addEventBtn = new JButton("+ Añadir");
        addEventBtn.addActionListener(e -> addEvent());
        JButton removeEventBtn = new JButton("- Eliminar");
        removeEventBtn.addActionListener(e -> removeEvent());
        eventBtnPanel.add(addEventBtn);
        eventBtnPanel.add(removeEventBtn);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(200, 0));
        leftPanel.add(eventScroll, BorderLayout.CENTER);
        leftPanel.add(eventBtnPanel, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout(0, 4));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Detalles del Evento"));

        // Propiedades base del evento
        JPanel propsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        propsPanel.add(new JLabel("Nombre:"));
        nameField = new JTextField(12);
        propsPanel.add(nameField);

        propsPanel.add(new JLabel("ID Único:"));
        idField = new JTextField(12);
        propsPanel.add(idField);

        propsPanel.add(new JLabel("Disparador:"));
        triggerCombo = new JComboBox<>(GameEvent.TriggerType.values());
        propsPanel.add(triggerCombo);

        JButton applyBtn = new JButton("Aplicar Cambios (Base)");
        applyBtn.addActionListener(e -> applyEventChanges());
        propsPanel.add(applyBtn);

        rightPanel.add(propsPanel, BorderLayout.NORTH);

        // Grafo de nodos e inspector
        nodeInspectorPanel = new NodeInspectorPanel(null); // lo configuramos en breve
        graphViewPanel = new GraphViewPanel(node -> nodeInspectorPanel.inspect(node));

        // Es importante hacer que el inspector conozca al graph para repintar
        // Por la inicialización, vamos a instanciarlo primero sin el panel, o
        // como GraphViewPanel ya le pasa la función, funciona. Pero actualizamos el
        // apuntador dentro.
        nodeInspectorPanel = new NodeInspectorPanel(graphViewPanel);
        // Regenerar el view con el nuevo lambda
        graphViewPanel = new GraphViewPanel(node -> nodeInspectorPanel.inspect(node));

        nodeInspectorPanel.setPreferredSize(new Dimension(250, 0));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphViewPanel, nodeInspectorPanel);
        splitPane.setResizeWeight(1.0); // GraphViewPanel toma el espacio extra
        splitPane.setDividerLocation(500);

        rightPanel.add(splitPane, BorderLayout.CENTER);

        add(rightPanel, BorderLayout.CENTER);
    }

    private void addEvent() {
        String name = JOptionPane.showInputDialog(this, "Nombre del evento:");
        if (name != null && !name.isBlank()) {
            String defaultId = name.toLowerCase().replace(" ", "_");
            String id = JOptionPane.showInputDialog(this, "ID Único del Evento:", defaultId);
            if (id == null)
                return;
            if (id.isEmpty())
                id = java.util.UUID.randomUUID().toString();

            GameEvent event = new GameEvent(name, GameEvent.TriggerType.AL_INTERACTUAR);
            event.setId(id);
            mainFrame.getProject().addEvent(event);
            refresh();
            eventList.setSelectedValue(event, true);
        }
    }

    private void removeEvent() {
        GameEvent event = eventList.getSelectedValue();
        if (event != null) {
            mainFrame.getProject().removeEvent(event);
            refresh();
        }
    }

    private void showEventDetails(GameEvent event) {
        if (event == null) {
            graphViewPanel.setEventGraph(null);
            nodeInspectorPanel.inspect(null);
            return;
        }
        updating = true;
        nameField.setText(event.getName());
        idField.setText(event.getId());
        triggerCombo.setSelectedItem(event.getTriggerType());

        graphViewPanel.setEventGraph(event.getEventGraph());
        nodeInspectorPanel.inspect(null); // Al cambiar de evento, limpiar inspector

        updating = false;
    }

    private void applyEventChanges() {
        if (updating)
            return;
        GameEvent event = eventList.getSelectedValue();
        if (event == null)
            return;

        event.setName(nameField.getText());
        event.setId(idField.getText().trim());
        event.setTriggerType((GameEvent.TriggerType) triggerCombo.getSelectedItem());

        refresh();
        eventList.setSelectedValue(event, true);
        mainFrame.setStatus("Evento actualizado: " + event.getName());
    }

    public void refresh() {
        eventListModel.clear();
        for (GameEvent event : mainFrame.getProject().getEvents()) {
            eventListModel.addElement(event);
        }
    }
}

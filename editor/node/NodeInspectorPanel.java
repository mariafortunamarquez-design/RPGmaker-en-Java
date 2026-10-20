/** @author María Fortuna */

package com.rpgmaker.editor.node;

import com.rpgmaker.model.ActionType;
import com.rpgmaker.model.event.*;

import javax.swing.*;
import java.awt.*;

/**
 * Panel derecho en la vista de eventos que inspecciona un EventNode
 * seleccionado
 */
public class NodeInspectorPanel extends JPanel {
    private JPanel propsPanel;
    private GraphViewPanel graphView;

    public NodeInspectorPanel(GraphViewPanel graphView) {
        this.graphView = graphView;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Propiedades del Nodo"));

        propsPanel = new JPanel(new GridBagLayout());
        JScrollPane scroll = new JScrollPane(propsPanel);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    public void inspect(EventNode nd) {
        propsPanel.removeAll();

        if (nd == null) {
            propsPanel.add(new JLabel("Añade o selecciona un nodo."));
            revalidate();
            repaint();
            return;
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row++;
        propsPanel.add(new JLabel("<b>Tipo:</b> " + nd.getNodeType()), gbc);

        if (nd instanceof DialogNode dn) {
            propsPanel.add(new JLabel("Texto del diálogo:"), gbc);

            gbc.gridy++;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 0.5;
            JTextArea textArea = new JTextArea(dn.getText(), 4, 15);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            propsPanel.add(new JScrollPane(textArea), gbc);

            gbc.gridy++;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weighty = 0;
            JButton applyBtn = new JButton("Aplicar");
            applyBtn.addActionListener(e -> {
                dn.setText(textArea.getText());
                graphView.repaint();
            });
            propsPanel.add(applyBtn, gbc);

        } else if (nd instanceof ActionNode an) {
            JComboBox<ActionType> combo = new JComboBox<>(ActionType.values());
            combo.setSelectedItem(an.getActionType());
            gbc.gridy = row++;
            propsPanel.add(new JLabel("Acción:"), gbc);
            gbc.gridy = row++;
            propsPanel.add(combo, gbc);

            JButton configureParamsBtn = new JButton("Configurar Parámetros");
            configureParamsBtn.addActionListener(e -> {
                an.setActionType((ActionType) combo.getSelectedItem());
                configureActionParameters(an);
                graphView.repaint();
            });
            gbc.gridy = row++;
            propsPanel.add(configureParamsBtn, gbc);

        } else if (nd instanceof ConditionNode cn) {
            propsPanel.add(new JLabel("Tipo Conidición:"), gbc);
            gbc.gridy++;
            JComboBox<ConditionNode.ConditionType> typeCombo = new JComboBox<>(ConditionNode.ConditionType.values());
            typeCombo.setSelectedItem(cn.getConditionType());
            propsPanel.add(typeCombo, gbc);

            gbc.gridy++;
            propsPanel.add(new JLabel("Target ID (Flag/Obj):"), gbc);
            gbc.gridy++;
            JTextField targetField = new JTextField(cn.getTargetId(), 15);
            propsPanel.add(targetField, gbc);

            gbc.gridy++;
            JCheckBox expectedCheck = new JCheckBox("Valor esperado (true/hold)", cn.isExpectedValue());
            propsPanel.add(expectedCheck, gbc);

            gbc.gridy++;
            JButton applyBtn = new JButton("Aplicar");
            applyBtn.addActionListener(e -> {
                cn.setConditionType((ConditionNode.ConditionType) typeCombo.getSelectedItem());
                cn.setTargetId(targetField.getText());
                cn.setExpectedValue(expectedCheck.isSelected());
                graphView.repaint();
            });
            propsPanel.add(applyBtn, gbc);

        } else if ("Start".equals(nd.getNodeType())) {
            gbc.gridy = row++;
            propsPanel.add(new JLabel("Punto de inicio del evento."), gbc);
        }

        // Espaciador final
        gbc.gridy = row;
        gbc.weighty = 1.0;
        propsPanel.add(new JPanel(), gbc);

        revalidate();
        repaint();
    }

    private void configureActionParameters(ActionNode action) {
        switch (action.getActionType()) {
            case DAR_OBJETO, QUITAR_OBJETO -> {
                String itemId = JOptionPane.showInputDialog(this, "Nombre/ID del objeto:",
                        action.getParameter("itemId"));
                String qty = JOptionPane.showInputDialog(this, "Cantidad:", action.getParameter("quantity"));
                if (itemId != null)
                    action.setParameter("itemId", itemId);
                if (qty != null && !qty.isEmpty())
                    action.setParameter("quantity", qty);
            }
            case TELETRANSPORTAR -> {
                String mapId = JOptionPane.showInputDialog(this, "ID del Mapa Destino:", action.getParameter("mapId"));
                String tx = JOptionPane.showInputDialog(this, "Posición X:", action.getParameter("x"));
                String ty = JOptionPane.showInputDialog(this, "Posición Y:", action.getParameter("y"));
                if (mapId != null)
                    action.setParameter("mapId", mapId);
                if (tx != null)
                    action.setParameter("x", tx);
                if (ty != null)
                    action.setParameter("y", ty);
            }
            case ESTABLECER_FLAG -> {
                String flagId = JOptionPane.showInputDialog(this, "Nombre de la flag:", action.getParameter("flagId"));
                String value = JOptionPane.showInputDialog(this, "Valor (true/false):",
                        action.getParameter("value") == null ? "true" : action.getParameter("value"));
                if (flagId != null)
                    action.setParameter("flagId", flagId);
                if (value != null)
                    action.setParameter("value", value);
            }
            case CURAR_JUGADOR, DAÑAR_JUGADOR -> {
                String amount = JOptionPane.showInputDialog(this, "Cantidad:", action.getParameter("amount"));
                if (amount != null)
                    action.setParameter("amount", amount);
            }
            case DAR_ORO, QUITAR_ORO -> {
                String amount = JOptionPane.showInputDialog(this, "Cantidad de oro:", action.getParameter("amount"));
                if (amount != null)
                    action.setParameter("amount", amount);
            }
            case DAR_EXP -> {
                String amount = JOptionPane.showInputDialog(this, "Cantidad de EXP:", action.getParameter("amount"));
                if (amount != null)
                    action.setParameter("amount", amount);
            }
            case INICIAR_BATALLA -> {
                String enemyId = JOptionPane.showInputDialog(this, "Nombre/ID del enemigo:",
                        action.getParameter("enemyId"));
                if (enemyId != null)
                    action.setParameter("enemyId", enemyId);
            }
            default -> {
            }
        }
    }
}

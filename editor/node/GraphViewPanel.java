/** @author María Fortuna */

package com.rpgmaker.editor.node;

import com.rpgmaker.model.event.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.CubicCurve2D;
import java.util.function.Consumer;

public class GraphViewPanel extends JPanel {

    private EventGraph graph;
    private int panX = 0, panY = 0;
    private Point dragStart;

    private EventNode selectedNode;
    private EventNode draggingNode;

    // Conexiones en progreso
    private EventNode connectingFromNode;
    private String connectingFromPort; // "next", "trueNext", "falseNext"
    private Point mousePos;

    // Selección
    private final int NODE_WIDTH = 140;
    private final int NODE_HEIGHT = 60;
    private final int PORT_RADIUS = 6;

    public GraphViewPanel(Consumer<EventNode> onNodeSelected) {
        setBackground(new Color(40, 40, 40));
        setFocusable(true);

        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                if (graph == null)
                    return;

                Point p = getModelPoint(e.getPoint());

                if (SwingUtilities.isRightMouseButton(e)) {
                    showContextMenu(e.getX(), e.getY(), p.x, p.y);
                    return;
                }

                // Verificar clics en puertos
                if (checkPortClick(p)) {
                    mousePos = e.getPoint();
                    return;
                }

                // Verificar clics en nodos
                EventNode clickedNode = getNodeAt(p);
                if (clickedNode != null) {
                    selectedNode = clickedNode;
                    draggingNode = clickedNode;
                    dragStart = e.getPoint();
                    if (onNodeSelected != null) {
                        onNodeSelected.accept(selectedNode);
                    }
                } else {
                    selectedNode = null;
                    dragStart = e.getPoint();
                    if (onNodeSelected != null) {
                        onNodeSelected.accept(null);
                    }
                }
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (graph == null)
                    return;

                if (connectingFromNode != null) {
                    mousePos = e.getPoint();
                    repaint();
                } else if (draggingNode != null) {
                    int dx = e.getX() - dragStart.x;
                    int dy = e.getY() - dragStart.y;
                    draggingNode.setX(draggingNode.getX() + dx);
                    draggingNode.setY(draggingNode.getY() + dy);
                    dragStart = e.getPoint();
                    repaint();
                } else if (dragStart != null && SwingUtilities.isLeftMouseButton(e)) {
                    // Panning
                    int dx = e.getX() - dragStart.x;
                    int dy = e.getY() - dragStart.y;
                    panX += dx;
                    panY += dy;
                    dragStart = e.getPoint();
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (connectingFromNode != null) {
                    Point p = getModelPoint(e.getPoint());
                    EventNode targetNode = getNodeAt(p);
                    if (targetNode != null && targetNode != connectingFromNode) {
                        // Crear conexión
                        if (connectingFromNode instanceof StartNode && "next".equals(connectingFromPort)) {
                            ((StartNode) connectingFromNode).setNextNodeId(targetNode.getId());
                        } else if (connectingFromNode instanceof DialogNode && "next".equals(connectingFromPort)) {
                            ((DialogNode) connectingFromNode).setNextNodeId(targetNode.getId());
                        } else if (connectingFromNode instanceof ActionNode && "next".equals(connectingFromPort)) {
                            ((ActionNode) connectingFromNode).setNextNodeId(targetNode.getId());
                        } else if (connectingFromNode instanceof ConditionNode) {
                            if ("trueNext".equals(connectingFromPort)) {
                                ((ConditionNode) connectingFromNode).setTrueNextNodeId(targetNode.getId());
                            } else if ("falseNext".equals(connectingFromPort)) {
                                ((ConditionNode) connectingFromNode).setFalseNextNodeId(targetNode.getId());
                            }
                        }
                    } else if (targetNode == null) {
                        // Romper conexión si se suelta en el vacío
                        if (connectingFromNode instanceof StartNode && "next".equals(connectingFromPort)) {
                            ((StartNode) connectingFromNode).setNextNodeId(null);
                        } else if (connectingFromNode instanceof DialogNode && "next".equals(connectingFromPort)) {
                            ((DialogNode) connectingFromNode).setNextNodeId(null);
                        } else if (connectingFromNode instanceof ActionNode && "next".equals(connectingFromPort)) {
                            ((ActionNode) connectingFromNode).setNextNodeId(null);
                        } else if (connectingFromNode instanceof ConditionNode) {
                            if ("trueNext".equals(connectingFromPort)) {
                                ((ConditionNode) connectingFromNode).setTrueNextNodeId(null);
                            } else if ("falseNext".equals(connectingFromPort)) {
                                ((ConditionNode) connectingFromNode).setFalseNextNodeId(null);
                            }
                        }
                    }
                    connectingFromNode = null;
                    connectingFromPort = null;
                    repaint();
                }

                draggingNode = null;
            }
        };

        addMouseListener(ma);
        addMouseMotionListener(ma);

        // Tecla Suprimir
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    if (selectedNode != null && !(selectedNode instanceof StartNode)) {
                        graph.removeNode(selectedNode.getId());
                        selectedNode = null;
                        if (onNodeSelected != null) {
                            onNodeSelected.accept(null);
                        }
                        repaint();
                    }
                }
            }
        });
    }

    public void setEventGraph(EventGraph graph) {
        this.graph = graph;
        this.selectedNode = null;
        this.panX = 0;
        this.panY = 0;
        repaint();
    }

    public EventGraph getEventGraph() {
        return this.graph;
    }

    public EventNode getSelectedNode() {
        return this.selectedNode;
    }

    private void showContextMenu(int viewX, int viewY, int modelX, int modelY) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem addDialog = new JMenuItem("Añadir Diálogo");
        addDialog.addActionListener(e -> {
            DialogNode n = new DialogNode();
            n.setX(modelX);
            n.setY(modelY);
            graph.addNode(n);
            repaint();
        });

        JMenuItem addAction = new JMenuItem("Añadir Acción");
        addAction.addActionListener(e -> {
            ActionNode n = new ActionNode();
            n.setX(modelX);
            n.setY(modelY);
            graph.addNode(n);
            repaint();
        });

        JMenuItem addCondition = new JMenuItem("Añadir Condición");
        addCondition.addActionListener(e -> {
            ConditionNode n = new ConditionNode();
            n.setX(modelX);
            n.setY(modelY);
            graph.addNode(n);
            repaint();
        });

        menu.add(addDialog);
        menu.add(addAction);
        menu.add(addCondition);
        menu.show(this, viewX, viewY);
    }

    private Point getModelPoint(Point viewPoint) {
        return new Point(viewPoint.x - panX, viewPoint.y - panY);
    }

    private EventNode getNodeAt(Point p) {
        for (EventNode node : graph.getNodes().values()) {
            Rectangle rect = new Rectangle(node.getX(), node.getY(), NODE_WIDTH, NODE_HEIGHT);
            if (node instanceof ConditionNode) {
                rect.height = 80;
            }
            if (rect.contains(p))
                return node;
        }
        return null;
    }

    private boolean checkPortClick(Point p) {
        for (EventNode node : graph.getNodes().values()) {
            int rightX = node.getX() + NODE_WIDTH;
            int h = node instanceof ConditionNode ? 80 : NODE_HEIGHT;
            int centerY = node.getY() + h / 2;

            if (node instanceof ConditionNode) {
                // Dos puertos de salida
                Rectangle rTrue = new Rectangle(rightX - PORT_RADIUS, node.getY() + 30 - PORT_RADIUS, PORT_RADIUS * 2,
                        PORT_RADIUS * 2);
                Rectangle rFalse = new Rectangle(rightX - PORT_RADIUS, node.getY() + 60 - PORT_RADIUS, PORT_RADIUS * 2,
                        PORT_RADIUS * 2);
                if (rTrue.contains(p)) {
                    connectingFromNode = node;
                    connectingFromPort = "trueNext";
                    return true;
                }
                if (rFalse.contains(p)) {
                    connectingFromNode = node;
                    connectingFromPort = "falseNext";
                    return true;
                }
            } else {
                // Un puerto de salida para Start, Dialog, Action
                Rectangle outPort = new Rectangle(rightX - PORT_RADIUS, centerY - PORT_RADIUS, PORT_RADIUS * 2,
                        PORT_RADIUS * 2);
                if (outPort.contains(p)) {
                    connectingFromNode = node;
                    connectingFromPort = "next";
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graph == null)
            return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Grid
        g2.setColor(new Color(60, 60, 60));
        int gridSize = 40;
        int sx = panX % gridSize;
        int sy = panY % gridSize;
        for (int x = sx - gridSize; x < getWidth(); x += gridSize) {
            g2.drawLine(x, 0, x, getHeight());
        }
        for (int y = sy - gridSize; y < getHeight(); y += gridSize) {
            g2.drawLine(0, y, getWidth(), y);
        }

        // Draw Connections
        g2.setStroke(new BasicStroke(2.0f));
        for (EventNode node : graph.getNodes().values()) {
            drawNodeConnections(g2, node);
        }

        // Draw ongoing connection
        if (connectingFromNode != null && mousePos != null) {
            drawOngoingConnection(g2);
        }

        // Draw Nodes
        for (EventNode node : graph.getNodes().values()) {
            drawNode(g2, node);
        }
    }

    private void drawNodeConnections(Graphics2D g2, EventNode node) {
        int nx = node.getX();
        int ny = node.getY();
        int outX = nx + NODE_WIDTH + panX;

        if (node instanceof ConditionNode cn) {
            int trueY = ny + 30 + panY;
            int falseY = ny + 60 + panY;
            drawCurve(g2, outX, trueY, cn.getTrueNextNodeId(), Color.GREEN);
            drawCurve(g2, outX, falseY, cn.getFalseNextNodeId(), Color.RED);
        } else {
            int outY = ny + NODE_HEIGHT / 2 + panY;
            String targetId = null;
            if (node instanceof StartNode)
                targetId = ((StartNode) node).getNextNodeId();
            else if (node instanceof DialogNode)
                targetId = ((DialogNode) node).getNextNodeId();
            else if (node instanceof ActionNode)
                targetId = ((ActionNode) node).getNextNodeId();

            drawCurve(g2, outX, outY, targetId, Color.WHITE);
        }
    }

    private void drawCurve(Graphics2D g2, int startX, int startY, String targetId, Color c) {
        if (targetId == null)
            return;
        EventNode tgt = graph.getNode(targetId);
        if (tgt != null) {
            int h = tgt instanceof ConditionNode ? 80 : NODE_HEIGHT;
            int endX = tgt.getX() + panX;
            int endY = tgt.getY() + h / 2 + panY;

            g2.setColor(c);
            drawSpline(g2, startX, startY, endX, endY);
        }
    }

    private void drawOngoingConnection(Graphics2D g2) {
        int nx = connectingFromNode.getX();
        int ny = connectingFromNode.getY();
        int outX = nx + NODE_WIDTH + panX;
        int outY;
        Color c = Color.WHITE;

        if (connectingFromNode instanceof ConditionNode) {
            if ("trueNext".equals(connectingFromPort)) {
                outY = ny + 30 + panY;
                c = Color.GREEN;
            } else {
                outY = ny + 60 + panY;
                c = Color.RED;
            }
        } else {
            outY = ny + NODE_HEIGHT / 2 + panY;
        }

        g2.setColor(c);
        drawSpline(g2, outX, outY, mousePos.x, mousePos.y);
    }

    private void drawSpline(Graphics2D g2, int x1, int y1, int x2, int y2) {
        int d = Math.max(Math.abs(x2 - x1) / 2, 40);
        CubicCurve2D c = new CubicCurve2D.Float(x1, y1, x1 + d, y1, x2 - d, y2, x2, y2);
        g2.draw(c);
    }

    private void drawNode(Graphics2D g2, EventNode node) {
        int x = node.getX() + panX;
        int y = node.getY() + panY;
        int w = NODE_WIDTH;
        int h = node instanceof ConditionNode ? 80 : NODE_HEIGHT;

        // Background
        g2.setColor(new Color(30, 30, 30, 220));
        g2.fillRoundRect(x, y, w, h, 10, 10);

        // Header
        g2.setColor(getHeaderColor(node));
        g2.fillRoundRect(x, y, w, 20, 10, 10);
        g2.fillRect(x, y + 10, w, 10); // flat bottom for header

        // Border
        if (node == selectedNode) {
            g2.setColor(Color.YELLOW);
            g2.setStroke(new BasicStroke(2.0f));
        } else {
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.0f));
        }
        g2.drawRoundRect(x, y, w, h, 10, 10);

        // Title
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.drawString(node.getNodeType(), x + 5, y + 14);

        // Content
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2.setColor(Color.LIGHT_GRAY);
        if (node instanceof DialogNode dn) {
            String txt = dn.getText();
            if (txt.length() > 20)
                txt = txt.substring(0, 18) + "...";
            g2.drawString(txt.isEmpty() ? "<vacio>" : txt, x + 5, y + 40);
        } else if (node instanceof ActionNode an) {
            g2.drawString(an.getActionType().getDisplayName(), x + 5, y + 40);
        } else if (node instanceof ConditionNode cn) {
            g2.drawString("Si: " + cn.getConditionType().name(), x + 5, y + 40);
            g2.drawString(cn.getTargetId(), x + 5, y + 55);
        }

        // Ports
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillOval(x - PORT_RADIUS, y + h / 2 - PORT_RADIUS, PORT_RADIUS * 2, PORT_RADIUS * 2);

        if (node instanceof ConditionNode) {
            g2.setColor(Color.GREEN);
            g2.fillOval(x + w - PORT_RADIUS, y + 30 - PORT_RADIUS, PORT_RADIUS * 2, PORT_RADIUS * 2);
            g2.setColor(Color.RED);
            g2.fillOval(x + w - PORT_RADIUS, y + 60 - PORT_RADIUS, PORT_RADIUS * 2, PORT_RADIUS * 2);
        } else {
            g2.setColor(Color.WHITE);
            g2.fillOval(x + w - PORT_RADIUS, y + h / 2 - PORT_RADIUS, PORT_RADIUS * 2, PORT_RADIUS * 2);
        }
    }

    private Color getHeaderColor(EventNode node) {
        if (node instanceof StartNode)
            return new Color(50, 150, 50);
        if (node instanceof DialogNode)
            return new Color(50, 100, 200);
        if (node instanceof ActionNode)
            return new Color(150, 100, 50);
        if (node instanceof ConditionNode)
            return new Color(180, 50, 50);
        return Color.GRAY;
    }
}

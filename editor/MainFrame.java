/** @author María Fortuna */

package com.rpgmaker.editor;

import com.rpgmaker.controller.MainController;
import com.rpgmaker.model.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * Ventana principal de la aplicación RPG Maker.
 */
public class MainFrame extends JFrame {

    private final MainController controller;
    private MapEditorPanel mapEditorPanel;
    private TilePalettePanel tilePalettePanel;
    private MapListPanel mapListPanel;
    private EntityEditorPanel entityEditorPanel;
    private ItemEditorPanel itemEditorPanel;
    private EventEditorPanel eventEditorPanel;
    private PlayerEditorPanel playerEditorPanel;
    private PropertiesPanel propertiesPanel;
    private JLabel statusLabel;
    private JTabbedPane rightTabs;

    public MainFrame(MainController controller) {
        super("RPG Maker — Edición Java");
        this.controller = controller;
        initUI();
    }

    private void showAbout() {
        JEditorPane card = new JEditorPane("text/html", """
                <html>
                <body style="font-family: sans-serif; font-size: 12pt; width: 340px;">
                  <h2>RPG Maker - Edición Java</h2>
                  <p>Versión 1.0</p>
                  <p>Herramienta para crear mapas y juegos de rol y exportarlos como código Java.</p>
                  <p>Creado por <b>María Fortuna</b>.</p>
                  <p><a href="https://mariafortunamarquez-design.github.io/MariaFortuna.github.io/">Tarjeta de presentación de María Fortuna</a></p>
                </body>
                </html>
                """);
        card.setEditable(false);
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        card.addHyperlinkListener(event -> {
            if (event.getEventType() == javax.swing.event.HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        throw new UnsupportedOperationException("Este sistema no permite abrir enlaces desde la aplicación.");
                    }
                    Desktop.getDesktop().browse(URI.create(event.getURL().toString()));
                } catch (IOException | UnsupportedOperationException ex) {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo abrir el enlace:\n" + ex.getMessage(),
                            "Error al abrir enlace", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        JOptionPane.showMessageDialog(this, card, "Acerca de", JOptionPane.INFORMATION_MESSAGE);
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });

        setSize(1400, 900);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);

        setJMenuBar(createMenuBar());

        add(createToolBar(), BorderLayout.NORTH);

        mapListPanel = new MapListPanel(this);
        JScrollPane mapListScroll = new JScrollPane(mapListPanel);
        mapListScroll.setPreferredSize(new Dimension(200, 0));
        mapListScroll.setBorder(BorderFactory.createTitledBorder("Mapas"));

        mapEditorPanel = new MapEditorPanel(this);
        JScrollPane mapEditorScroll = new JScrollPane(mapEditorPanel);
        mapEditorScroll.getHorizontalScrollBar().setUnitIncrement(16);
        mapEditorScroll.getVerticalScrollBar().setUnitIncrement(16);

        rightTabs = new JTabbedPane(JTabbedPane.TOP);
        rightTabs.setPreferredSize(new Dimension(280, 0));

        tilePalettePanel = new TilePalettePanel(this);
        rightTabs.addTab("Casillas", new JScrollPane(tilePalettePanel));

        propertiesPanel = new PropertiesPanel(this);
        rightTabs.addTab("Propiedades", new JScrollPane(propertiesPanel));

        entityEditorPanel = new EntityEditorPanel(this);
        rightTabs.addTab("Entidades", new JScrollPane(entityEditorPanel));

        itemEditorPanel = new ItemEditorPanel(this);
        rightTabs.addTab("Objetos", new JScrollPane(itemEditorPanel));

        eventEditorPanel = new EventEditorPanel(this);
        rightTabs.addTab("Eventos", new JScrollPane(eventEditorPanel));

        playerEditorPanel = new PlayerEditorPanel(this);
        rightTabs.addTab("Jugador", new JScrollPane(playerEditorPanel));

        JSplitPane rightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mapEditorScroll, rightTabs);
        rightSplit.setResizeWeight(1.0);
        rightSplit.setDividerLocation(900);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mapListScroll, rightSplit);
        mainSplit.setDividerLocation(200);

        add(mainSplit, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusLabel = new JLabel("Listo");
        statusBar.add(statusLabel);
        add(statusBar, BorderLayout.SOUTH);

        refreshAll();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menú Archivo
        JMenu fileMenu = new JMenu("Archivo");
        fileMenu.setMnemonic(KeyEvent.VK_A);

        JMenuItem newItem = new JMenuItem("Nuevo Proyecto");
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newItem.addActionListener(e -> newProject());

        JMenuItem openItem = new JMenuItem("Abrir Proyecto...");
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openItem.addActionListener(e -> openProject());

        JMenuItem saveItem = new JMenuItem("Guardar Proyecto");
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveItem.addActionListener(e -> saveProject());

        JMenuItem saveAsItem = new JMenuItem("Guardar Como...");
        saveAsItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        saveAsItem.addActionListener(e -> saveProjectAs());

        JMenuItem exportItem = new JMenuItem("Exportar como Proyecto Java...");
        exportItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
        exportItem.addActionListener(e -> exportProject());

        JMenuItem exitItem = new JMenuItem("Salir");
        exitItem.addActionListener(e -> confirmExit());

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exportItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Menú Editar
        JMenu editMenu = new JMenu("Editar");
        editMenu.setMnemonic(KeyEvent.VK_E);

        JMenuItem projSettings = new JMenuItem("Configuración del Proyecto...");
        projSettings.addActionListener(e -> showProjectSettings());
        editMenu.add(projSettings);

        // Menú Mapa
        JMenu mapMenu = new JMenu("Mapa");
        mapMenu.setMnemonic(KeyEvent.VK_M);

        JMenuItem addMap = new JMenuItem("Añadir Nuevo Mapa");
        addMap.addActionListener(e -> mapListPanel.addNewMap());

        JMenuItem resizeMap = new JMenuItem("Redimensionar Mapa Actual...");
        resizeMap.addActionListener(e -> resizeCurrentMap());

        mapMenu.add(addMap);
        mapMenu.add(resizeMap);

        // Menú Herramientas
        JMenu toolsMenu = new JMenu("Herramientas");
        toolsMenu.setMnemonic(KeyEvent.VK_H);

        ButtonGroup toolGroup = new ButtonGroup();
        JRadioButtonMenuItem paintTool = new JRadioButtonMenuItem("Pintar", true);
        paintTool.addActionListener(e -> mapEditorPanel.setCurrentTool(MapEditorPanel.Tool.PAINT));
        JRadioButtonMenuItem eraseTool = new JRadioButtonMenuItem("Borrar");
        eraseTool.addActionListener(e -> mapEditorPanel.setCurrentTool(MapEditorPanel.Tool.ERASE));
        JRadioButtonMenuItem fillTool = new JRadioButtonMenuItem("Rellenar");
        fillTool.addActionListener(e -> mapEditorPanel.setCurrentTool(MapEditorPanel.Tool.FILL));
        JRadioButtonMenuItem entityTool = new JRadioButtonMenuItem("Colocar Entidad");
        entityTool.addActionListener(e -> mapEditorPanel.setCurrentTool(MapEditorPanel.Tool.ENTITY));
        JRadioButtonMenuItem selectTool = new JRadioButtonMenuItem("Seleccionar");
        selectTool.addActionListener(e -> mapEditorPanel.setCurrentTool(MapEditorPanel.Tool.SELECT));

        toolGroup.add(paintTool);
        toolGroup.add(eraseTool);
        toolGroup.add(fillTool);
        toolGroup.add(entityTool);
        toolGroup.add(selectTool);

        toolsMenu.add(paintTool);
        toolsMenu.add(eraseTool);
        toolsMenu.add(fillTool);
        toolsMenu.add(entityTool);
        toolsMenu.add(selectTool);

        // Menú Recursos
        JMenu resourcesMenu = new JMenu("Recursos");
        resourcesMenu.setMnemonic(KeyEvent.VK_R);

        JMenuItem importMusicItem = new JMenuItem("Importar Música (WAV)...");
        importMusicItem.addActionListener(e -> importMusic());
        resourcesMenu.add(importMusicItem);

        // Menú Ayuda
        JMenu helpMenu = new JMenu("Ayuda");
        JMenuItem aboutItem = new JMenuItem("Acerca de");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);

        JMenuItem tutorialItem = new JMenuItem("Tutorial Interactivo (Paso a paso)");
        tutorialItem.addActionListener(e -> startComplexTutorial());
        helpMenu.add(tutorialItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(mapMenu);
        menuBar.add(toolsMenu);
        menuBar.add(resourcesMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton newBtn = new JButton("Nuevo");
        newBtn.setFocusable(false);
        newBtn.addActionListener(e -> newProject());

        JButton openBtn = new JButton("Abrir");
        openBtn.setFocusable(false);
        openBtn.addActionListener(e -> openProject());

        JButton saveBtn = new JButton("Guardar");
        saveBtn.setFocusable(false);
        saveBtn.addActionListener(e -> saveProject());

        toolBar.add(newBtn);
        toolBar.add(openBtn);
        toolBar.add(saveBtn);
        toolBar.addSeparator();

        // Botones de herramientas
        JToggleButton paintBtn = new JToggleButton("Pintar", true);
        JToggleButton eraseBtn = new JToggleButton("Borrar");
        JToggleButton fillBtn = new JToggleButton("Rellenar");
        JToggleButton entityBtn = new JToggleButton("Entidad");
        JToggleButton selectBtn = new JToggleButton("Seleccionar");

        ButtonGroup toolGroup = new ButtonGroup();
        toolGroup.add(paintBtn);
        toolGroup.add(eraseBtn);
        toolGroup.add(fillBtn);
        toolGroup.add(entityBtn);
        toolGroup.add(selectBtn);

        paintBtn.addActionListener(e -> mapEditorPanel.setCurrentTool(MapEditorPanel.Tool.PAINT));
        eraseBtn.addActionListener(e -> mapEditorPanel.setCurrentTool(MapEditorPanel.Tool.ERASE));
        fillBtn.addActionListener(e -> mapEditorPanel.setCurrentTool(MapEditorPanel.Tool.FILL));
        entityBtn.addActionListener(e -> mapEditorPanel.setCurrentTool(MapEditorPanel.Tool.ENTITY));
        selectBtn.addActionListener(e -> mapEditorPanel.setCurrentTool(MapEditorPanel.Tool.SELECT));

        paintBtn.setFocusable(false);
        eraseBtn.setFocusable(false);
        fillBtn.setFocusable(false);
        entityBtn.setFocusable(false);
        selectBtn.setFocusable(false);

        toolBar.add(paintBtn);
        toolBar.add(eraseBtn);
        toolBar.add(fillBtn);
        toolBar.add(entityBtn);
        toolBar.add(selectBtn);
        toolBar.addSeparator();

        // Zoom
        JButton zoomInBtn = new JButton("Acercar");
        zoomInBtn.setFocusable(false);
        zoomInBtn.addActionListener(e -> mapEditorPanel.zoomIn());
        JButton zoomOutBtn = new JButton("Alejar");
        zoomOutBtn.setFocusable(false);
        zoomOutBtn.addActionListener(e -> mapEditorPanel.zoomOut());

        toolBar.add(zoomInBtn);
        toolBar.add(zoomOutBtn);
        toolBar.addSeparator();

        JButton previewBtn = new JButton("Vista Previa");
        previewBtn.setFocusable(false);
        previewBtn.addActionListener(e -> launchPreview());
        toolBar.add(previewBtn);

        JButton exportBtn = new JButton("Exportar");
        exportBtn.setFocusable(false);
        exportBtn.addActionListener(e -> exportProject());
        toolBar.add(exportBtn);

        return toolBar;
    }

    private void newProject() {
        int choice = JOptionPane.showConfirmDialog(this,
                "¿Crear un nuevo proyecto? Los cambios no guardados se perderán.",
                "Nuevo Proyecto", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            controller.createProject();
            refreshAll();
            setStatus("Nuevo proyecto creado");
        }
    }

    private void openProject() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos de Proyecto RPG (.rpg)", "rpg"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                controller.openProject(chooser.getSelectedFile());
                refreshAll();
                setStatus("Abierto: " + chooser.getSelectedFile().getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al abrir proyecto:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void saveProject() {
        if (controller.getProject().getFilePath() != null) {
            try {
                File file = new File(controller.getProject().getFilePath());
                controller.saveProject(file);
                setStatus("Guardado: " + file.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al guardar:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            saveProjectAs();
        }
    }

    private void saveProjectAs() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos de Proyecto RPG (.rpg)", "rpg"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().endsWith(".rpg")) {
                file = new File(file.getAbsolutePath() + ".rpg");
            }
            try {
                controller.saveProject(file);
                setStatus("Guardado: " + file.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al guardar:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportProject() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Elegir Directorio de Exportación");
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                controller.exportProject(chooser.getSelectedFile());
                JOptionPane.showMessageDialog(this,
                        "Juego exportado correctamente.\n\nPara compilar, ejecute compilar.bat.\nPara jugar, ejecute ejecutar.bat.",
                        "Exportación Completa", JOptionPane.INFORMATION_MESSAGE);
                setStatus("Exportado a: " + chooser.getSelectedFile().getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error en exportación:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void showProjectSettings() {
        JTextField nameField = new JTextField(controller.getProject().getName(), 20);
        JTextField authorField = new JTextField(controller.getProject().getAuthor(), 20);
        JTextArea descArea = new JTextArea(controller.getProject().getDescription(), 3, 20);
        JTextField versionField = new JTextField(controller.getProject().getVersion(), 10);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nombre del Juego:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Autor:"), gbc);
        gbc.gridx = 1;
        panel.add(authorField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(descArea), gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Versión:"), gbc);
        gbc.gridx = 1;
        panel.add(versionField, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Configuración del Proyecto",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            controller.updateProjectSettings(nameField.getText(), authorField.getText(),
                    descArea.getText(), versionField.getText());
            setStatus("Configuración del proyecto actualizada");
        }
    }

    private void resizeCurrentMap() {
        GameMap map = getCurrentMap();
        if (map == null)
            return;

        SpinnerNumberModel widthModel = new SpinnerNumberModel(map.getWidth(), 5, 200, 1);
        SpinnerNumberModel heightModel = new SpinnerNumberModel(map.getHeight(), 5, 200, 1);
        JSpinner widthSpinner = new JSpinner(widthModel);
        JSpinner heightSpinner = new JSpinner(heightModel);

        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.add(new JLabel("Ancho:"));
        panel.add(widthSpinner);
        panel.add(new JLabel("Alto:"));
        panel.add(heightSpinner);

        int result = JOptionPane.showConfirmDialog(this, panel, "Redimensionar Mapa",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            controller.resizeMap(map, (int) widthSpinner.getValue(), (int) heightSpinner.getValue());
            mapEditorPanel.repaint();
            mapEditorPanel.revalidate();
        }
    }

    private void launchPreview() {
        GameMap currentMap = getCurrentMap();
        if (currentMap == null) {
            JOptionPane.showMessageDialog(this, "No hay mapa para previsualizar.", "Vista Previa",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        com.rpgmaker.preview.GamePreviewPanel previewPanel = new com.rpgmaker.preview.GamePreviewPanel(
                controller.getProject(), currentMap);
        JDialog dialog = new JDialog(this, "Vista Previa — " + currentMap.getName(), true);
        dialog.setContentPane(previewPanel);
        dialog.setSize(800, 640);
        dialog.setLocationRelativeTo(this);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                previewPanel.stopMusic();
            }
        });
        dialog.setVisible(true);
    }

    private void importMusic() {
        GameMap map = getCurrentMap();
        if (map == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un mapa primero.",
                    "Importar Música", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Seleccionar Archivo de Música (WAV)");
        fc.setFileFilter(new FileNameExtensionFilter("Archivos de Audio (WAV)", "wav"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fc.getSelectedFile().getAbsolutePath();
            map.setBgmPath(path);
            map.setBgmName(fc.getSelectedFile().getName());
            setStatus("Música asignada al mapa '" + map.getName() + "': " + fc.getSelectedFile().getName());
        }
    }

    private void confirmExit() {
        int choice = JOptionPane.showConfirmDialog(this,
                "¿Salir de RPG Maker? Los cambios no guardados se perderán.",
                "Confirmar Salida", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    private void startComplexTutorial() {
        String[] titles = {
                "1. Gestión de Mapas",
                "2. Paleta de Casillas",
                "3. Editor de Mapas",
                "4. Editor de Entidades",
                "5. Editor de Eventos",
                "6. Vista Previa"
        };

        String[] contents = {
                "<html>Este es el <b>Panel de Mapas</b>.<br><br>Aquí puedes crear, eliminar y renombrar tus niveles.<br>Recuerda asignar un <b>ID Único</b> que tenga sentido (ej: 'bosque_1') para poder usar teletransportes luego.</html>",
                "<html>En la pestaña <b>Casillas</b> eliges con qué pintar.<br><br>Selecciona un tipo (Hierba, Agua, etc.) y una textura personalizada si lo deseas.</html>",
                "<html>Este es el <b>Lienzo Principal</b>.<br><br>Usa 'Pintar' para texturizar, o 'Entidad' para colocar personajes.<br>Dato: Usa <b>Ctrl + Rueda</b> para hacer zoom.</html>",
                "<html>En <b>Entidades</b> configuras a los personajes.<br><br>Puedes darles diálogos, stats de combate o asignarles un <b>Evento</b> para que ocurran cosas al tocarlos.</html>",
                "<html>¡Lo más importante! <b>Eventos</b>.<br><br>Aquí creas la lógica: diálogos, cofres y <b>Teletransportes</b>.<br>Usa el ID del mapa destino para conectar tus mundos.</html>",
                "<html>Finalmente, usa el botón de <b>Vista Previa</b>.<br><br>Prueba tus teletransportes y diálogos en tiempo real antes de exportar.</html>"
        };

        JComponent[] targets = {
                mapListPanel,
                tilePalettePanel,
                mapEditorPanel,
                entityEditorPanel,
                eventEditorPanel,
                null // Preview btn se resalta solo con texto por ahora
        };

        for (int i = 0; i < titles.length; i++) {
            // Preparar UI
            if (i == 1)
                rightTabs.setSelectedIndex(0);
            if (i == 3)
                rightTabs.setSelectedIndex(2);
            if (i == 4)
                rightTabs.setSelectedIndex(4);

            highlightComponent(targets[i], true);

            Object[] options = (i == titles.length - 1) ? new Object[] { "Finalizar" }
                    : new Object[] { "Siguiente", "Salir" };
            int choice = JOptionPane.showOptionDialog(this,
                    contents[i], titles[i],
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);

            highlightComponent(targets[i], false);

            if (choice != 0 && i < titles.length - 1)
                break;
        }
    }

    private void highlightComponent(JComponent c, boolean enable) {
        if (c == null)
            return;
        if (enable) {
            c.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.YELLOW, 4),
                    c.getBorder()));
            c.scrollRectToVisible(new Rectangle(0, 0, 10, 10));
        } else {
            if (c.getBorder() instanceof javax.swing.border.CompoundBorder cb) {
                c.setBorder(cb.getInsideBorder());
            }
        }
    }

    public void refreshAll() {
        mapListPanel.refresh();
        if (!controller.getProject().getMaps().isEmpty()) {
            setCurrentMap(controller.getProject().getMaps().get(0));
        }
        entityEditorPanel.refresh();
        itemEditorPanel.refresh();
        eventEditorPanel.refresh();
        playerEditorPanel.refresh();
        setTitle("RPG Maker — " + controller.getProject().getName());
    }

    public GameProject getProject() {
        return controller.getProject();
    }

    public GameMap getCurrentMap() {
        return mapEditorPanel.getCurrentMap();
    }

    public void setCurrentMap(GameMap map) {
        mapEditorPanel.setCurrentMap(map);
        propertiesPanel.showMapProperties(map);
        entityEditorPanel.refresh();
        setStatus("Editando: " + (map != null ? map.getName() : "ninguno"));
    }

    public TileType getSelectedTileType() {
        return tilePalettePanel.getSelectedTileType();
    }

    public MapEditorPanel getMapEditorPanel() {
        return mapEditorPanel;
    }

    public EntityEditorPanel getEntityEditorPanel() {
        return entityEditorPanel;
    }

    public void setStatus(String text) {
        statusLabel.setText(text);
    }

    public void showEntityTab() {
        rightTabs.setSelectedComponent(rightTabs.getComponentAt(2)); // Pestaña Entidades
    }

    /**
     * Devuelve la ruta de la textura personalizada seleccionada en la paleta, o
     * null.
     */
    public String getSelectedTexturePath() {
        return tilePalettePanel.getSelectedTexturePath();
    }
}

/** @author María Fortuna */

package com.rpgmaker.preview;

import com.rpgmaker.model.Entity;
import com.rpgmaker.util.ImageCache;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Panel de combate visual clásico (estilo JRPG).
 */
public class CombatPanel extends JPanel {

    private GamePreviewPanel parent;
    private Entity enemy;
    private int playerHp, playerMaxHp;
    private int playerAtk, playerDef;
    private int enemyHp;

    private CombatState state;
    private Timer timer;

    private int menuSelection = 0; // 0: Atacar, 1: Defender, 2: Huir
    private String combatLog = "";
    private int animationTicks = 0;

    private boolean isDefending = false;

    public CombatPanel(GamePreviewPanel parent, Entity enemy, int playerHp, int playerMaxHp, int pAtk, int pDef,
            int pSpd) {
        this.parent = parent;
        this.enemy = enemy;
        this.playerHp = playerHp;
        this.playerMaxHp = playerMaxHp;
        this.playerAtk = pAtk;
        this.playerDef = pDef;
        this.enemyHp = enemy.getMaxHp();

        this.state = CombatState.INTRO;
        this.combatLog = "¡Un " + enemy.getName() + " salvaje aparece!";

        setBackground(Color.BLACK);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleInput(e.getKeyCode());
            }
        });

        // Main game loop para animaciones y delays
        timer = new Timer(50, e -> {
            updateCombat();
            repaint();
        });
        timer.start();
    }

    private void handleInput(int keyCode) {
        if (state == CombatState.PLAYER_TURN) {
            if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) {
                menuSelection--;
                if (menuSelection < 0)
                    menuSelection = 2;
            } else if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S) {
                menuSelection++;
                if (menuSelection > 2)
                    menuSelection = 0;
            } else if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_E || keyCode == KeyEvent.VK_SPACE) {
                executePlayerAction();
            }
        } else if (state == CombatState.VICTORY || state == CombatState.DEFEAT) {
            if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_E || keyCode == KeyEvent.VK_SPACE) {
                endCombat();
            }
        } else if (state == CombatState.INTRO) {
            if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_E || keyCode == KeyEvent.VK_SPACE) {
                state = CombatState.PLAYER_TURN;
                combatLog = "¿Qué harás?";
            }
        }
    }

    private void executePlayerAction() {
        if (menuSelection == 0) {
            // Atacar
            isDefending = false;
            int dmg = Math.max(1, playerAtk - enemy.getDefense());
            enemyHp -= dmg;
            combatLog = "¡Atacas! Causas " + dmg + " de daño.";
            state = CombatState.PLAYER_ANIMATION;
            animationTicks = 0;
        } else if (menuSelection == 1) {
            // Defender
            isDefending = true;
            combatLog = "¡Te defiendes y te preparas para el impacto!";
            state = CombatState.PLAYER_ANIMATION;
            animationTicks = 0;
        } else if (menuSelection == 2) {
            // Huir
            if (Math.random() > 0.5) {
                combatLog = "¡Has escapado con éxito!";
                state = CombatState.VICTORY; // Tratar como fin
            } else {
                combatLog = "¡No pudiste escapar!";
                state = CombatState.PLAYER_ANIMATION;
                animationTicks = 0;
            }
        }
    }

    private void updateCombat() {
        if (state == CombatState.PLAYER_ANIMATION) {
            animationTicks++;
            if (animationTicks > 20) {
                if (enemyHp <= 0) {
                    state = CombatState.VICTORY;
                    combatLog = "¡Has derrotado al " + enemy.getName() + "!";
                } else {
                    state = CombatState.ENEMY_TURN;
                    animationTicks = 0;
                }
            }
        } else if (state == CombatState.ENEMY_TURN) {
            // Lógica simple IA: Ataca
            int eAtk = enemy.getAttack();
            int def = isDefending ? playerDef * 2 : playerDef;
            int dmg = Math.max(1, eAtk - def);
            playerHp -= dmg;
            combatLog = "¡El enemigo ataca! Te causa " + dmg + " de daño.";
            state = CombatState.ENEMY_ANIMATION;
            animationTicks = 0;
        } else if (state == CombatState.ENEMY_ANIMATION) {
            animationTicks++;
            if (animationTicks > 20) {
                if (playerHp <= 0) {
                    state = CombatState.DEFEAT;
                    combatLog = "¡Has sido derrotado por " + enemy.getName() + "...";
                } else {
                    state = CombatState.PLAYER_TURN;
                    combatLog = "¿Qué harás?";
                }
            }
        }
    }

    private void endCombat() {
        timer.stop();
        if (state == CombatState.VICTORY && enemyHp <= 0) { // No huida
            parent.onCombatEnd(true, enemy, playerHp);
        } else if (state == CombatState.DEFEAT) {
            parent.onCombatEnd(false, enemy, playerHp);
        } else {
            // Huida
            parent.onCombatEnd(true, null, playerHp); // null signify escape
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 1. Draw Enemy
        int ew = 150;
        int eh = 150;
        int ex = w / 2 - ew / 2;
        int ey = 50;

        if (state == CombatState.PLAYER_ANIMATION && menuSelection == 0 && animationTicks % 4 < 2) {
            // Parpadeo de daño
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }

        Image img = null;
        if (enemy.getImagePath() != null) {
            img = ImageCache.getImage(enemy.getImagePath());
        }

        if (img != null) {
            g2.drawImage(img, ex, ey, ew, eh, this);
        } else {
            g2.setColor(enemy.getType().getColor());
            g2.fillRoundRect(ex, ey, ew, eh, 20, 20);
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        // 2. Draw Enemy HP Bar
        int eBarW = 100;
        g2.setColor(Color.RED);
        g2.fillRect(ex + ew / 2 - eBarW / 2, ey - 20, eBarW, 8);
        g2.setColor(Color.GREEN);
        int curEW = (int) ((double) Math.max(0, enemyHp) / enemy.getMaxHp() * eBarW);
        g2.fillRect(ex + ew / 2 - eBarW / 2, ey - 20, curEW, 8);
        g2.setColor(Color.WHITE);
        g2.drawRect(ex + ew / 2 - eBarW / 2, ey - 20, eBarW, 8);

        // 3. Draw Bottom UI Panel
        int panelH = 150;
        g2.setColor(new Color(0, 0, 150));
        g2.fillRoundRect(10, h - panelH - 10, w - 20, panelH, 15, 15);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4.0f));
        g2.drawRoundRect(10, h - panelH - 10, w - 20, panelH, 15, 15);

        // Dividir Panel: Log y Menú / Status Jugador
        g2.setFont(new Font("Monospaced", Font.BOLD, 18));

        if (state == CombatState.PLAYER_TURN) {
            // Dibujar Menú
            g2.drawString("Atacar", 50, h - panelH + 30);
            g2.drawString("Defender", 50, h - panelH + 70);
            g2.drawString("Huir", 50, h - panelH + 110);

            // Cursor
            int cursorY = h - panelH + 22 + (menuSelection * 40);
            g2.drawString(">", 25, cursorY);
        } else {
            // Dibujar Log
            g2.drawString(combatLog, 40, h - panelH + 60);
        }

        // 4. Status Jugador (Derecha del panel)
        int statX = w - 250;
        g2.drawLine(statX - 20, h - panelH - 10, statX - 20, h - 10);
        g2.drawString("Héroe", statX, h - panelH + 30);
        g2.setFont(new Font("Monospaced", Font.BOLD, 14));
        g2.drawString("HP: " + playerHp + " / " + playerMaxHp, statX, h - panelH + 70);

        // Barra HP jugador
        int pBarW = 120;
        g2.setColor(Color.RED);
        g2.fillRect(statX, h - panelH + 90, pBarW, 10);
        g2.setColor(Color.GREEN);
        int curPW = (int) ((double) Math.max(0, playerHp) / playerMaxHp * pBarW);
        g2.fillRect(statX, h - panelH + 90, curPW, 10);
        g2.setColor(Color.WHITE);
        g2.drawRect(statX, h - panelH + 90, pBarW, 10);

        if (state == CombatState.ENEMY_ANIMATION && animationTicks % 4 < 2) {
            g2.setColor(new Color(255, 0, 0, 100)); // Flash pantalla rojo
            g2.fillRect(0, 0, w, h);
        }
    }
}

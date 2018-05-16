package com.fossgalaxy.games.tbs.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by webpigeon on 22/01/18.
 */
public class KeyboardController implements KeyListener {
    private UIModel model;
    private GameView view;

    public KeyboardController(UIModel model, GameView view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_SHIFT) {
            model.setShowingLocations(true);
            view.repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_SHIFT) {
            model.setShowingLocations(false);
            view.repaint();
        }
    }
}

package com.fossgalaxy.games.tbs.ui;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.io.IOUtils;
import com.fossgalaxy.games.tbs.order.Order;
import org.codetome.hexameter.core.api.CubeCoordinate;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by webpigeon on 22/01/18.
 */
public class MouseController implements MouseListener {
    private GameView view;
    private UIModel model;

    public MouseController(GameView view, UIModel model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        GameState state = model.getState();

        CubeCoordinate location = state.pix2cube(mouseEvent.getPoint());

        // Left click select, right click move

        if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
            Entity entity = state.getEntityAt(location);
            if (entity != null) {
                model.setHighlight(entity);
            }
            model.setSelected(location);
            view.repaint();
        } else if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
            Entity currEntity = model.getCurrHighlight();
            if (currEntity == null) {
                return;
            }


            GameAction action = model.getAction();
            if (action == null) {
                action = model.getBestAction(location);
            }

            if (action != null) {
                Order currOrder = action.generateOrder(location, state);
                if (currOrder != null) {
                    model.addOrder(currEntity, currOrder);
                }
            }

        }

        view.repaint();
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
}

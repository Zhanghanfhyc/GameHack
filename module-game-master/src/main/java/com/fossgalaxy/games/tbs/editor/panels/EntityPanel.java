package com.fossgalaxy.games.tbs.editor.panels;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.editor.EditorListener;
import com.fossgalaxy.games.tbs.parameters.EntityType;

import javax.swing.*;
import java.awt.*;

public class EntityPanel extends JPanel {

    private EntitySelector selector;
    private DefaultComboBoxModel<Integer> comboBoxModel;

    public EntityPanel(java.util.List<EntityType> types, EditorListener listener){
        super(new BorderLayout());

        this.selector = new EntitySelector(types, listener);
        add(selector);

        this.comboBoxModel = new DefaultComboBoxModel<>();
        comboBoxModel.addElement(0);
        comboBoxModel.addElement(1);

        //default to selecting 0
        listener.setOwner(0);

        JComboBox<Integer> comboBox = new JComboBox<>(comboBoxModel);
        comboBox.setEditable(true);

        //when you change the selection, update the box to reflect that.
        comboBox.addActionListener(actionEvent -> {
            Object selected = comboBoxModel.getSelectedItem();
            if (selected instanceof Integer) {
                listener.setOwner((Integer)selected);
            } else if (selected instanceof String) {
                String selectedStr = (String)selected;

                try {
                    listener.setOwner(Integer.parseInt(selectedStr));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(comboBox,
                            String.format("'%s' is not an integer", selectedStr),
                            "Error parsing int",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        buildPlayerSelect(comboBox);
    }

    protected void buildPlayerSelect(JComboBox<Integer> comboBox) {
        Box box = Box.createHorizontalBox();

        box.add(Box.createHorizontalStrut(10));

        JLabel label = new JLabel("Owner");
        label.setLabelFor(comboBox);
        box.add(label);

        box.add(Box.createHorizontalStrut(10));
        box.add(comboBox);

        add(box, BorderLayout.NORTH);
    }
}

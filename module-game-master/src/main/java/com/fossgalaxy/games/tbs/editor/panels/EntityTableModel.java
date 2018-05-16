package com.fossgalaxy.games.tbs.editor.panels;

import com.fossgalaxy.games.tbs.entity.Entity;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class EntityTableModel extends AbstractTableModel {
    public static String[] COLS = {"name", "value"};
    private static String[] FIXED_PROPERTY_NAMES = {"owner"};


    private static final int EXTRA_ROW = 1;

    private static final int COL_NAME = 0;
    private static final int COL_VALUE = 1;

    private final List<String> propertyNames;

    private Entity entity;

    public EntityTableModel() {
        this.propertyNames = new ArrayList<>();
    }

    public void setEntity(Entity entity) {
        this.entity = entity;

        this.propertyNames.clear();
        this.propertyNames.addAll(entity.getPropertyNames());

        this.fireTableDataChanged();
    }

    protected int getRealId(int row) {
        return row - FIXED_PROPERTY_NAMES.length;
    }

    @Override
    public int getRowCount() {
        return FIXED_PROPERTY_NAMES.length + propertyNames.size() + EXTRA_ROW;
    }

    @Override
    public int getColumnCount() {
        return COLS.length;
    }

    @Override
    public String getColumnName(int i) {
        return COLS[i];
    }

    @Override
    public Class<?> getColumnClass(int i) {
        if (i == 0) {
            return String.class;
        } else if (i == 1) {
            return Integer.class;
        }

        throw new IllegalArgumentException("Got an unknown column?!");
    }

    @Override
    public boolean isCellEditable(int row, int col) {

        //you can't edit values for properties that don't exist yet...
        if (col == COL_VALUE && row == getRowCount()-1) {
            return false;
        }

        //can't edit fixed properties - they're hard coded
        if (col == COL_NAME && row < FIXED_PROPERTY_NAMES.length) {
            return false;
        }

        //everything else can be edited
        return true;
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (entity == null) {
            return 0;
        }

        int realID = getRealId(row);

        if (row >= FIXED_PROPERTY_NAMES.length){
            if (realID >= propertyNames.size()) {
                if (col == 0)
                    return "";
                else {
                    return 0;
                }
            }

            String propName = propertyNames.get(realID);
            if (col == COL_NAME) {
                return  propName;
            } else {
                return entity.getProperty(propName);
            }
        } else {
            if (col == COL_NAME) {
                return FIXED_PROPERTY_NAMES[row];
            } else {
                switch (row) {
                    case 0:
                        return entity.getOwner();
                    default:
                        throw new IllegalArgumentException("Got an unknown column?!");
                }

            }

        }
    }

    @Override
    public void setValueAt(Object o, int row, int col) {
        if (row >= FIXED_PROPERTY_NAMES.length) {
            int realID = getRealId(row);

            if (col == COL_VALUE) {
                //editing a value

                //if you are attempting to edit the value of a new column, go away.
                if (realID >= propertyNames.size()) {
                    return;
                }

                //updating the value of an existing property
                Integer newVal = (Integer) o;
                String property = propertyNames.get(realID);
                entity.setProperty(property, newVal);

            } else if (col == COL_NAME) {
                //editing a name

                String newName = (String)o;
                newName = newName.trim();

                if (realID >= propertyNames.size()) {
                    if (newName.equals("")) {
                        return;
                    }

                   // we're creating a new property
                   propertyNames.add(newName);
                   entity.setProperty(newName, 0);

                   fireTableRowsInserted(row, row);
                   return;
                } else {
                    //we're trying to rename an existing property
                    String oldName = propertyNames.get(realID);
                    int oldVal = entity.getProperty(oldName);

                    entity.removeProperty(oldName);

                    //setting the name to blank kills it.
                    if (!newName.equals("")) {
                        entity.setProperty(newName, oldVal);
                        propertyNames.set(realID, newName);
                        fireTableCellUpdated(row, col);
                    } else {
                        propertyNames.remove(oldName);
                        fireTableRowsDeleted(row, row);
                    }
                }

            }
        } else {
            if (col == COL_VALUE) {
                Integer newVal = (Integer) o;

                switch (row) {
                    case 0:
                        entity.setOwner(newVal);
                        break;
                    default:
                        throw new IllegalArgumentException("Got an unknown column?!");
                }

            }
        }

        fireTableCellUpdated(row, col);
    }

}

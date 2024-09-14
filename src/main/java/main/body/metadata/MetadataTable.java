package main.body.metadata;

import jakarta.inject.Inject;
import main.state.IStateManager;
import main.state.ISubscriber;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MetadataTable implements IMetadataTable, ISubscriber {

    private final IStateManager stateManager;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private IMetadataEditor metadataEditor;

    @Inject
    public MetadataTable(IStateManager stateManager, IMetadataEditor metadataEditor) {
        stateManager.subscribe(this);
        this.stateManager = stateManager;
        this.metadataEditor = metadataEditor;
        tableModel = new DefaultTableModel(new String[]{"Object", "Type", "Add"}, 0);
        table = new JTable(tableModel);
        table.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(), metadataEditor));
        table.getColumnModel().getColumn(2).setPreferredWidth(30);
        TableCellRenderer renderer = new EvenOddRenderer();
        table.setDefaultRenderer(Object.class, renderer);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    @Override
    public JPanel getPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    @Override
    public JTable getTable() {
        return table;
    }

    @Override
    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    @Override
    public void update() {
        Map<String, String> meta = stateManager.getState().metadata;

        List<Map.Entry<String, String>> sortedMetadata = new ArrayList<>(meta.entrySet());
        sortedMetadata.sort(Map.Entry.comparingByKey());

        tableModel.setRowCount(0);

        for (Map.Entry<String, String> entry : sortedMetadata) {
            String key = entry.getKey();
            String value = entry.getValue();

            if ("object".equals(value)) {
                tableModel.addRow(new Object[]{key, value, "+"});
            } else {
                tableModel.addRow(new Object[]{key, value});
            }
        }
    }

    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    public static class EvenOddRenderer implements TableCellRenderer {

        public static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            DEFAULT_RENDERER.setHorizontalAlignment(JLabel.CENTER);
            Component renderer = DEFAULT_RENDERER.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            ((JLabel) renderer).setOpaque(true);
            Color foreground, background;

            Color lightBlue = new Color(234, 234, 234);

            if (isSelected) {
                foreground = Color.BLACK;
                background = new Color(187, 108, 30, 128);
            } else {
                if (row % 2 == 0) {
                    foreground = Color.black;
                    background = Color.white;
                } else {
                    foreground = Color.black;
                    background = lightBlue;
                }
            }

            renderer.setForeground(foreground);
            renderer.setBackground(background);
            return renderer;
        }
    }
    private static class ButtonEditor extends DefaultCellEditor {
        private String label;
        private boolean isPushed;
        private IMetadataEditor metadataEditor;

        public ButtonEditor(JCheckBox checkBox, IMetadataEditor metadataEditor) {
            super(checkBox);
            this.metadataEditor = metadataEditor;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (value == null) {
                return null;
            }
            label = value.toString();
            JButton button = new JButton(label);
            button.addActionListener(e -> {
                fireEditingStopped();
                handleButtonClick(table, row, column);
            });
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                System.out.println("Button clicked");
            }
            isPushed = false;
            return label;
        }

        private void handleButtonClick(JTable table, int row, int column) {
            isPushed = true;
            String object = metadataEditor.getMetadataPane().getNameField().getText();
            String type = metadataEditor.getMetadataPane().getTypeComboBox().getSelectedItem().toString();
            metadataEditor.addMetadataForObject(object, type);
        }
    }
}

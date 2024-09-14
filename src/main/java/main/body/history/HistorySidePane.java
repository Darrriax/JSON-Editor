package main.body.history;

import com.google.inject.Inject;
import main.state.IStateManager;
import main.state.ISubscriber;
import main.state.State;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HistorySidePane implements IHistoryPane, ISubscriber {
    private JTable historyTable;
    private IStateManager stateManager;
    private IJsonDisplayer jsonDisplayer;

    @Inject
    public HistorySidePane(IStateManager stateManager, IJsonDisplayer jsonDisplayer) {
        stateManager.subscribe(this);
        this.stateManager = stateManager;
        this.jsonDisplayer = jsonDisplayer;
        historyTable = new JTable(new DefaultTableModel(new Object[]{"History"}, 0));
        updateHistoryTable();
        addTableSelectionListener();
    }

    private void addTableSelectionListener() {
        historyTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = historyTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        List<State> history = stateManager.getHistory();
                        State selectedState = history.get(selectedRow);
                        jsonDisplayer.displayJson(selectedState.json);
                    }
                }
            }
        });
    }

    private void updateHistoryTable() {
        DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
        model.setRowCount(0);

        List<State> history = stateManager.getHistory();
        for (State state : history) {
            model.addRow(new Object[]{state.toString()});
        }
    }

    @Override
    public JPanel getPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JButton checkButton = new JButton("Rollback");
        checkButton.addActionListener(e -> {
            int selectedRow = historyTable.getSelectedRow();
            if (selectedRow >= 0) {
                List<State> history = stateManager.getHistory();
                State selectedState = history.get(selectedRow);
                stateManager.rollbackToSelectedState(selectedState);
            }
        });

        panel.add(checkButton, BorderLayout.SOUTH);
        JScrollPane scrollPane = new JScrollPane(historyTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    @Override
    public void update() {
        updateHistoryTable();
    }
}

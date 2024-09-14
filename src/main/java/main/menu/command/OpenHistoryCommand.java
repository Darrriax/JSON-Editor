package main.menu.command;

import main.body.history.IHistoryManager;
import main.state.IStateManager;

import javax.swing.*;

public class OpenHistoryCommand implements ICommand {
    private final IStateManager stateManager;
    private final IHistoryManager historyManager;

    public OpenHistoryCommand(IStateManager stateManager, IHistoryManager historyManager) {
        this.stateManager = stateManager;
        this.historyManager = historyManager;
    }

    private void showHistory() {
        JDialog dialog = new JDialog();
        dialog.setTitle("History Manager");
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(historyManager.getPanel());
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Override
    public void execute() {
        showHistory();
    }
}

package main.body.history;

import com.google.inject.Inject;
import main.state.IStateManager;
import main.state.ISubscriber;

import javax.swing.*;
import java.awt.*;

public class HistoryManager implements IHistoryManager, ISubscriber {
    private final IJsonDisplayer jsonDisplayer;
    private final IHistoryPane historyPane;
    private final IStateManager stateManager;

    @Inject
    public HistoryManager(IStateManager stateManager) {
        stateManager.subscribe(this);
        this.stateManager = stateManager;
        jsonDisplayer = new JsonDisplayer(this.stateManager);
        historyPane = new HistorySidePane(this.stateManager, this.jsonDisplayer);
    }

    @Override
    public JPanel getPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, historyPane.getPanel(), jsonDisplayer.getPanel());
        splitPane.setDividerLocation(200);
        panel.add(splitPane, BorderLayout.CENTER);
        panel.setSize(800, 600);
        return panel;
    }

    @Override
    public void update() {
    }
}

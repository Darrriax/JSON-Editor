package main.body.history;

import com.google.inject.Inject;
import main.state.IStateManager;
import main.state.ISubscriber;
import main.state.State;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import javax.swing.*;
import java.awt.*;

public class JsonDisplayer implements IJsonDisplayer, ISubscriber {

    private final IStateManager stateManager;
    private final RSyntaxTextArea jsonArea;

    @Inject
    public JsonDisplayer(IStateManager stateManager) {
        this.stateManager = stateManager;
        this.stateManager.subscribe(this);

        RSyntaxTextArea jsonTextArea = new RSyntaxTextArea();
        jsonTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);

        State state = this.stateManager.getState();
        jsonTextArea.setText(state.json);
        this.jsonArea = jsonTextArea;
    }

    @Override
    public JPanel getPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(this.jsonArea), BorderLayout.CENTER);
        return panel;
    }

    @Override
    public void update() {
        State state = this.stateManager.getState();
        this.jsonArea.setText(state.json);
    }

    @Override
    public void displayJson(String json) {
        this.jsonArea.setText(json);
    }
}

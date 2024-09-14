package main.body.json;

import com.google.inject.Inject;
import main.state.IStateManager;
import main.state.ISubscriber;
import main.state.State;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JsonEditor implements IJsonEditor, ISubscriber {
    private final IStateManager stateManager;
    private final RSyntaxTextArea jsonArea;
    private final Timer inactivityTimer;
    RTextScrollPane scrollPane;
    RSyntaxTextArea jsonTextArea;

    @Inject
    public JsonEditor(IStateManager stateManager) {
        this.stateManager = stateManager;
        this.stateManager.subscribe(this);
        jsonTextArea = new RSyntaxTextArea();
        jsonTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
        scrollPane = new RTextScrollPane(jsonTextArea);

        State state = this.stateManager.getState();
        jsonTextArea.setText(state.json);
        this.jsonArea = jsonTextArea;

        jsonTextArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                textChanged();
            }

            public void removeUpdate(DocumentEvent e) {
                textChanged();
            }

            public void changedUpdate(DocumentEvent e) {
                // Plain text components do not fire these events
            }

            private void textChanged() {
                State state = stateManager.getState();
                state.json = jsonTextArea.getText();
                inactivityTimer.restart();
            }
        });

        inactivityTimer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        inactivityTimer.setRepeats(false);
    }

    @Override
    public JPanel getPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        scrollPane.setLineNumbersEnabled(true);
        scrollPane.setViewportView(jsonTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    @Override
    public void update() {
        State state = this.stateManager.getState();
        this.jsonArea.setText(state.json);
    }
}

package main.body;

import com.google.inject.Inject;
import main.body.json.IJsonEditor;
import main.body.metadata.IMetadataEditor;
import main.body.metadata.MetadataPane;
import main.body.validator.IValidatorComponent;
import main.state.IStateManager;
import main.state.ISubscriber;

import javax.swing.*;
import java.awt.*;

public class AppBody extends JFrame implements IAppBody, ISubscriber {

    private final IStateManager stateManager;
    private final IJsonEditor jsonEditor;
    private final IMetadataEditor metadataEditor;
    private final IValidatorComponent validatorComponent;
    private JPanel mainPanel;
    private boolean openedFile = false;

    @Inject
    public AppBody(IStateManager stateManager, IJsonEditor jsonEditor, IMetadataEditor metadataEditor, IValidatorComponent validatorComponent) {
        stateManager.subscribe(this);
        this.stateManager = stateManager;
        this.metadataEditor = metadataEditor;
        this.jsonEditor = jsonEditor;
        this.validatorComponent = validatorComponent;
        this.mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(204, 204, 204));

        JLabel helloLabel = new JLabel("Створіть новий файл чи відкрийте існуючий");
        helloLabel.setForeground(Color.BLACK);
        helloLabel.setHorizontalAlignment(SwingConstants.CENTER);

        mainPanel.add(helloLabel, BorderLayout.CENTER);
    }

    @Override
    public JPanel getPanel() {
        return this.mainPanel;
    }

    @Override
    public void update() {
        if (stateManager.getState().currentJsonFile != null) {
            openedFile = true;
        } else {
            openedFile = false;
        }

        updateUI();
    }

    private void updateUI() {
        SwingUtilities.invokeLater(() -> {
            mainPanel.removeAll();
            if (openedFile) {

                MetadataPane pane = new MetadataPane(metadataEditor);
                metadataEditor.setMetadataPane(pane);

                JPanel metadata = metadataEditor.getPanel();
                JPanel json = jsonEditor.getPanel();
                JPanel valid = validatorComponent.getPanel();

                Dimension preferredSize = new Dimension(100, metadata.getPreferredSize().height);
                metadata.setPreferredSize(preferredSize);
                Dimension preferredHigh = new Dimension(valid.getPreferredSize().width, 80);
                valid.setPreferredSize(preferredHigh);

                JSplitPane topSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, metadata, json);
                topSplitPane.setResizeWeight(0.1);
                JSplitPane bottomSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplitPane, valid);
                mainPanel.setLayout(new BorderLayout());
                mainPanel.add(topSplitPane, BorderLayout.CENTER);
                mainPanel.add(bottomSplitPane, BorderLayout.SOUTH);
            } else {
                mainPanel.removeAll();
                mainPanel.setLayout(new BorderLayout());
                mainPanel.setBackground(new Color(204, 204, 204));

                JLabel helloLabel = new JLabel("Створіть новий файл чи відкрийте існуючий");
                helloLabel.setForeground(Color.BLACK);
                helloLabel.setHorizontalAlignment(SwingConstants.CENTER);

                mainPanel.add(helloLabel, BorderLayout.CENTER);
                mainPanel.revalidate();
                mainPanel.repaint();
            }

            mainPanel.revalidate();
            mainPanel.repaint();
        });
    }
}

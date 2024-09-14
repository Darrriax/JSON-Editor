package main;

import com.google.inject.Inject;
import main.body.IAppBody;

import javax.swing.*;

public class JsonToolApp implements IJsonToolApp
{
    private final JFrame frame;

    @Inject
    public JsonToolApp(JMenuBar menuBar, IAppBody appBody)
    {
        this.frame = new JFrame("JSON Tool App");
        this.frame.setSize(900, 700);
        this.frame.setLocationRelativeTo(null);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.frame.setJMenuBar(menuBar);
        this.frame.setContentPane(appBody.getPanel());
    }

    @Override
    public void run()
    {
        this.frame.setVisible(true);
    }
}

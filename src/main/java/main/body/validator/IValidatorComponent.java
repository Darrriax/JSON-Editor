package main.body.validator;

import main.IPanelProvider;

import java.awt.*;

public interface IValidatorComponent extends IPanelProvider
{
    void updateMessagePane(String message, Color color);
    boolean validateMetadata();
}

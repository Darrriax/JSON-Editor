package main.body.validator;

import com.google.inject.Inject;

import java.awt.*;

public class MessageDisplay implements IMessageDisplay {
    private final IValidatorComponent validatorComponent;

    @Inject
    public MessageDisplay(IValidatorComponent validatorComponent) {
        this.validatorComponent = validatorComponent;
    }

    public void showError(String message) {
        this.validatorComponent.updateMessagePane(message, MessageType.ERROR.getColor());
    }

    public void showSuccess(String message) {
        this.validatorComponent.updateMessagePane(message, MessageType.SUCCESS.getColor());
    }

    public void showWarning(String message) {
        this.validatorComponent.updateMessagePane(message, MessageType.WARNING.getColor());
    }

    private enum MessageType {
        ERROR(new Color(170, 0, 0)),
        SUCCESS(new Color(0, 126, 0)),
        WARNING(new Color(186, 88, 0));

        private final Color color;

        MessageType(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }
    }
}

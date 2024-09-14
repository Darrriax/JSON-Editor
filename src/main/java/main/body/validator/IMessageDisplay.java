package main.body.validator;

public interface IMessageDisplay {
    void showError(String message);
    void showSuccess(String message);
    void showWarning(String message);
}

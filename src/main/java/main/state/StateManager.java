package main.state;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Singleton
public class StateManager implements IStateManager {
    private State currentState;
    private Stack<State> history;
    private List<ISubscriber> subscribers;

    @Inject
    public StateManager() {
        this.currentState = new State();
        this.history = new Stack<>();
        this.subscribers = new ArrayList<>();
    }

    @Override
    public void update() {
        this.subscribers.forEach(ISubscriber::update);
    }

    @Override
    public void subscribe(ISubscriber subscriber) {
        this.subscribers.add(subscriber);
    }

    @Override
    public void undo() {
        if (!history.isEmpty()) {
            this.currentState = history.pop();
            update();
        }
    }

    @Override
    public void save() {
        history.push(cloneState(this.currentState));
        JOptionPane.showMessageDialog(null, "Зміни збережено в історію");
    }

    @Override
    public void rollbackToSelectedState(State selectedState) {
        while (!history.isEmpty() && !history.peek().equals(selectedState)) {
            history.pop();
        }
        this.currentState = cloneState(selectedState);
        update();
    }

    @Override
    public List<State> getHistory() {
        return history;
    }

    @Override
    public State getState() {
        return this.currentState;
    }

    @Override
    public void setState(State currentState) {
        this.currentState = currentState;
    }

    private State cloneState(State state) {
        return new State(state);
    }
}

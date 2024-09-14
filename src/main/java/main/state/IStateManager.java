package main.state;

import java.util.List;

public interface IStateManager
{
    void update();

    void subscribe(ISubscriber subscriber);

    void undo();

    void save();

    State getState();

    void setState(State currentState);

    void rollbackToSelectedState(State selectedState);

    List<State> getHistory();
}

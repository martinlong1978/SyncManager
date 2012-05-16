package com.martinutils.sync.operations;

import java.util.ArrayList;
import java.util.List;

public class Conflict<O> extends Action<O>
{

    private List<Action<O>> conflictActions = new ArrayList<Action<O>>();

    public Conflict(Action<O> action)
    {
        super(Operations.CONFLICT, null, null);
        addConfictAction(action);
    }

    public void setTask(Runnable task)
    {
        this.task = task;
    }

    public void addConfictAction(Action<O> action)
    {
        if (action instanceof Conflict)
        {
            conflictActions.addAll(((Conflict<O>) action).getConflictActions());
        }
        else
        {
            conflictActions.add(action);
        }
    }

    public List<Action<O>> getConflictActions()
    {
        return conflictActions;
    }

}

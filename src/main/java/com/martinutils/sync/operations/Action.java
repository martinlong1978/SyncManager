package com.martinutils.sync.operations;

import com.martinutils.sync.IItemSummary;

public class Action<O>
{

    private Operations op;

    protected Runnable task;

    private IItemSummary<O> summary;

    public Action(Operations op, IItemSummary<O> summary, Runnable task)
    {
        this.op = op;
        this.task = task;
        this.summary = summary;
    }

    public Operations getOperation()
    {
        return op;
    }

    public Runnable getTask()
    {
        return task;
    }

    public IItemSummary<O> getSummary()
    {
        return summary;
    }
}

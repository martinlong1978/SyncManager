package com.martinutils.sync.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.martinutils.sync.IItemSummary;
import com.martinutils.sync.IProvider;

/**
 * Group of summaries from different providers that all represent the same
 * Object
 * 
 * @param <O>
 */
public class SummaryGroup<O>
{

    private static final long serialVersionUID = -5919415255713697970L;

    private final Map<IProvider<O>, IItemSummary<O>> summaries = new HashMap<IProvider<O>, IItemSummary<O>>();

    public Collection<IItemSummary<O>> getSummaries()
    {
        return summaries.values();
    }

    public void addSummary(IItemSummary<O> summary)
    {
        summary.setSummaryGroup(this);
        summaries.put(summary.getProvider(), summary);
    }

    public IItemSummary<O> getSummaryForProvider(IProvider<O> provider)
    {
        return summaries.get(provider);
    }

}

package com.martinutils.sync.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.martinutils.sync.IItemSummary;
import com.martinutils.sync.IProviderStore;

public class ProviderStore<O> implements IProviderStore<O>, Serializable
{

    private final Map<String, IItemSummary<O>> items = new HashMap<String, IItemSummary<O>>();

    private transient Map<String, IItemSummary<O>> tempSummaries = new HashMap<String, IItemSummary<O>>();

    @Override
    public IItemSummary<O> getItemSummary(String id)
    {
        tempSummaries.remove(id);
        return items.get(id);
    }

    @Override
    public void addItemSummary(IItemSummary<O> itemSummary)
    {
        System.out.println("Adding: " + itemSummary);
        items.put(itemSummary.getIdentifier(), itemSummary);
        // tempSummaries.put(itemSummary.getIdentifier(), itemSummary);
    }

    @Override
    public void reset()
    {
        tempSummaries = new HashMap<String, IItemSummary<O>>(items);
    }

    @Override
    public Collection<IItemSummary<O>> getUnreadSummaries()
    {
        return tempSummaries.values();
    }

    @Override
    public IItemSummary<O> deleteItemSummary(String id)
    {
        tempSummaries.remove(id);
        return items.remove(id);
    }

}

package com.martinutils.sync.impl;

import java.util.List;

import com.martinutils.sync.IItemSummary;
import com.martinutils.sync.IProviderStore;

public class ProviderStore<O> implements IProviderStore<O>
{

    @Override
    public IItemSummary<O> getItemSummary(String id)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addItemSummary(IItemSummary<O> itemSummary)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void reset()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public List<IItemSummary<O>> getUnreadSummaries()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IItemSummary<O> deleteItemSummary(String id)
    {
        // TODO Auto-generated method stub
        return null;
    }

}

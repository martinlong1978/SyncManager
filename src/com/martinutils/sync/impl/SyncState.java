package com.martinutils.sync.impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.martinutils.sync.IProvider;

public class SyncState<O>
{

    private List<SummaryGroup<O>> groups = new ArrayList<SummaryGroup<O>>();

    private Hashtable<IProvider<O>, ProviderStore<O>> stores = new Hashtable<IProvider<O>, ProviderStore<O>>();

    public ProviderStore<O> getStoreForProvider(IProvider<O> provider)
    {
        return stores.get(provider);
    }

}

package com.martinutils.sync.impl;

import java.util.Hashtable;

import com.martinutils.sync.IProvider;

public class SyncState<O>
{

    private final Hashtable<String, ProviderStore<O>> stores = new Hashtable<String, ProviderStore<O>>();

    public ProviderStore<O> getStoreForProvider(IProvider<O> provider)
    {
        return stores.get(provider.getName());
    }

}

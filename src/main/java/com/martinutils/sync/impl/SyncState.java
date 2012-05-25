package com.martinutils.sync.impl;

import java.util.Hashtable;

import com.martinutils.sync.IProvider;

public class SyncState<O>
{

    private final Hashtable<String, ProviderStore<O>> stores = new Hashtable<String, ProviderStore<O>>();

    public ProviderStore<O> getStoreForProvider(IProvider<O> provider)
    {
        ProviderStore<O> providerStore = stores.get(provider.getName());
        if (providerStore == null)
        {
            providerStore = new ProviderStore<O>();
            stores.put(provider.getName(), providerStore);
        }
        return providerStore;
    }

}

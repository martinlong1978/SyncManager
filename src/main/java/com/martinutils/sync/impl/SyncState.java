package com.martinutils.sync.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;

import com.martinutils.sync.IProvider;

public class SyncState<O> implements Serializable
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

    public static <T> SyncState<T> fromBytes(byte[] bytes) throws IOException, ClassNotFoundException
    {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(is);
        return (SyncState<T>) ois.readObject();
    }

    public byte[] asBytes() throws IOException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(this);
        oos.flush();
        return os.toByteArray();
    }

}

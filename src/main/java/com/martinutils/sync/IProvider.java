package com.martinutils.sync;

import java.util.List;

public interface IProvider<O>
{

    public void deleteObject(String id);

    /**
     * Insert this object into the provider.
     * 
     * @param object
     *            The object to be inserted
     * @return A new or existing summary group.
     */
    public IItemSummary<O> insertObject(O object);

    public IItemSummary<O> updateObject(String id, O object);

    public O fetchObject(String id);

    public List<IItemSummary<O>> getSummaries();

    public void setProviderStore(IProviderStore<O> store);

    public IProviderStore<O> getProviderStore();

    public String getName();

}

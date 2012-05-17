package com.martinutils.sync;

public interface IProvider<O>
{

    void deleteObject(String id);

    /**
     * Insert this object into the provider.
     * 
     * @param object
     *            The object to be inserted
     * @return A new or existing summary group.
     */
    IItemSummary<O> insertObject(O object);

    IItemSummary<O> updateObject(String id, O object);

    /**
     * There may be cases when there first sync occurs when both ends already
     * contain equivalent data (for example after a backup is restored). If the
     * provider can determine equivalence with an existing record then it can
     * return that from here. Extend IItemSummary if you need additional details
     * to determine equivalence.
     * 
     * Create the new summary and return it.
     * 
     * @param object
     * @return
     */
    IItemSummary<O> findEquivalent(IItemSummary<O> summary);

    O fetchObject(String id);

    IItemSummary<O>[] getSummaries();

    void setProviderStore(IProviderStore<O> store);

    IProviderStore<O> getProviderStore();

}

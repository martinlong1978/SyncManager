package com.martinutils.sync;

public interface IProvider<O>
{

    void deleteObject(String id);

    /**
     * Insert this object into the provider.
     * 
     * There may be cases when there first sync occurs when both ends already
     * contain equivalent data (for example after a backup is restored). If the
     * provider can determine equivalence with an existing record, then it
     * should not do the insert, but instead return the existing IItemSummary
     * for that item. It must use this opportunity to resolve any conflict based
     * on the data in object.
     * 
     * In those cases a conflict will not be reported, but a separate insert
     * call will be made on each provider.
     * 
     * @param object
     *            The object to be inserted
     * @return A new or existing summary group.
     */
    IItemSummary<O> insertObject(O object);

    IItemSummary<O> updateObject(String id, O object);

    O fetchObject(String id);

    IItemSummary<O>[] getSummaries();

    void setProviderStore(IProviderStore<O> store);

    IProviderStore<O> getProviderStore();

}

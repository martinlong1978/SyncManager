package com.martinutils.sync;

import java.util.Collection;

public interface IProviderStore<O>
{
    /**
     * 
     * Reset all summaries to unread.
     */
    public void reset();

    public IItemSummary<O> getItemSummary(String id);

    public IItemSummary<O> deleteItemSummary(String id);

    public Collection<IItemSummary<O>> getUnreadSummaries();

    public void addItemSummary(IItemSummary<O> itemSummary);

}

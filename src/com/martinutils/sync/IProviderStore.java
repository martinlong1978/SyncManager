package com.martinutils.sync;

import java.util.List;

public interface IProviderStore<O>
{
    /**
     * 
     * Reset all summaries to unread.
     */
    void reset();

    IItemSummary<O> getItemSummary(String id);

    IItemSummary<O> deleteItemSummary(String id);

    List<IItemSummary<O>> getUnreadSummaries();

    void addItemSummary(IItemSummary<O> itemSummary);

}

package com.martinutils.sync;

import com.martinutils.sync.impl.SummaryGroup;

/**
 * 
 * @author martin
 */
public interface IItemSummary<O>
{

    /**
     * Get the provider for this summary.
     * 
     * @return The provider
     */
    IProvider<O> getProvider();

    /**
     * The provider specific identifier for this object.
     * 
     * @return The provider specific identifier for this object.
     */
    String getIdentifier();

    /**
     * A hash for the object. This could be a genuine hash of the object or a
     * modified date. If you don't support updates this can be null.
     * 
     * @return A string representation of an object hash.
     */
    String getHash();

    void setSummaryGroup(SummaryGroup<O> group);

    SummaryGroup<O> getSummaryGroup();

}

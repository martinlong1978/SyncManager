package com.martinutils.sync;

import java.io.Serializable;

import com.martinutils.sync.impl.SummaryGroup;

/**
 * 
 * @author martin
 */
public interface IItemSummary<O> extends Serializable
{

    /**
     * Get the provider for this summary.
     * 
     * @return The provider
     */
    public String getProviderName();

    /**
     * The provider specific identifier for this object.
     * 
     * @return The provider specific identifier for this object.
     */
    public String getIdentifier();

    public String getGlobalID();

    /**
     * A hash for the object. This could be a genuine hash of the object or a
     * modified date. If you don't support updates this can be null.
     * 
     * @return A string representation of an object hash.
     */
    public String getHash();

    public void setSummaryGroup(SummaryGroup<O> group);

    public SummaryGroup<O> getSummaryGroup();

}

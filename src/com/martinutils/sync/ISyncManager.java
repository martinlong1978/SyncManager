package com.martinutils.sync;

/**
 * 
 * @author martin
 * 
 * @param <O>
 *            The object type being synced
 */
public interface ISyncManager<O>
{

    public void addConflictListener(IConflictListener<O> listener);

    public void addProvider(IProvider<O> provider);

    public void syncProviders();

}

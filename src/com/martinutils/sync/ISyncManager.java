package com.martinutils.sync;

/**
 * 
 * @author martin
 * 
 * @param <P>
 *            enum type, describing the providers
 * @param <O>
 *            The object type being synced
 */
public interface ISyncManager<O>
{

    void addConflictListener(IConflictListener<O> listener);

    void addProvider(IProvider<O> provider);

    void syncProviders();

}

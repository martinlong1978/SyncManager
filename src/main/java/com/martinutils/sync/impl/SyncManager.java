package com.martinutils.sync.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.martinutils.sync.IConflictListener;
import com.martinutils.sync.IItemSummary;
import com.martinutils.sync.IProvider;
import com.martinutils.sync.IProviderStore;
import com.martinutils.sync.ISyncManager;
import com.martinutils.sync.operations.Action;
import com.martinutils.sync.operations.Conflict;
import com.martinutils.sync.operations.Operations;

public class SyncManager<O> implements ISyncManager<O>
{

    private final HashMap<String, IProvider<O>> providers = new HashMap<String, IProvider<O>>();

    private final Map<SummaryGroup<O>, Action<O>> deferredActions = new HashMap<SummaryGroup<O>, Action<O>>();

    private final Map<String, SummaryGroup<O>> equivalenceGroups = new HashMap<String, SummaryGroup<O>>();

    private IConflictListener<O> listener;

    private final SyncState<O> syncState;

    public SyncManager()
    {
        syncState = new SyncState<O>();
    }

    public SyncManager(byte[] bytes) throws IOException, ClassNotFoundException
    {
        syncState = SyncState.fromBytes(bytes);
    }

    @Override
    public void addProvider(IProvider<O> provider)
    {
        providers.put(provider.getName(), provider);
        provider.setProviderStore(syncState.getStoreForProvider(provider));
    }

    @Override
    public void syncProviders()
    {

        // Store these... we don't want to fetch again for the second pass
        List<IItemSummary<O>[]> providerSummaries = new ArrayList<IItemSummary<O>[]>();

        // First pass, determines if there are any equivalents that shouldn't be
        // added.
        for (IProvider<O> provider : providers.values())
        {
            IItemSummary<O>[] summaries = provider.getSummaries();
            providerSummaries.add(summaries);
            for (IItemSummary<O> summary : summaries)
            {
                String globalID = summary.getGlobalID();
                if (globalID != null)
                {
                    SummaryGroup<O> equivalenceGroup = equivalenceGroups.get(globalID);
                    if (equivalenceGroup == null)
                    {
                        equivalenceGroup = new SummaryGroup<O>();
                        equivalenceGroups.put(globalID, equivalenceGroup);
                    }
                    equivalenceGroup.addSummary(summary);
                }

            }
        }

        // Second pass... now determine the actions
        Iterator<IProvider<O>> iterator = providers.values().iterator();
        for (IItemSummary<O>[] summaries : providerSummaries)
        {
            processProvider(summaries, iterator.next());
        }
    }

    private void processProvider(IItemSummary<O>[] summaries, IProvider<O> provider)
    {
        IProviderStore<O> store = provider.getProviderStore();
        store.reset();
        for (IItemSummary<O> summary : summaries)
        {
            String identifier = summary.getIdentifier();
            IItemSummary<O> storedSummary = store.getItemSummary(identifier);
            if (storedSummary == null)
            {
                // Item has been added.
                addItem(summary);
            }
            else
            {
                checkForNewProviders(storedSummary);
                if (!storedSummary.getHash().equals(summary.getHash()))
                {
                    // Item has been updated.
                    updateItem(storedSummary, summary);
                }
            }
        }
        for (IItemSummary<O> summary : store.getUnreadSummaries())
        {
            System.out.println(summary);
            // Item has been deleted
            deleteItem(summary);
        }
        // Run deferred actions
        for (Action<O> action : deferredActions.values())
        {
            action.getTask().run();
        }

    }

    /**
     * If a new provider has been added, there won't be ANY records, so this
     * adds them
     * 
     * @param storedSummary
     */
    private void checkForNewProviders(final IItemSummary<O> storedSummary)
    {
        for (IProvider<O> provider : providers.values())
        {
            final IItemSummary<O> providerSummary = storedSummary.getSummaryGroup().getSummaryForProvider(provider);
            if (providerSummary == null)
            {
                final O fetchObject = getProviderForSummary(storedSummary).fetchObject(storedSummary.getIdentifier());
                SummaryGroup<O> summaryGroup = storedSummary.getSummaryGroup();
                IItemSummary<O> updatedSummary = provider.insertObject(fetchObject);
                summaryGroup.addSummary(updatedSummary);
                getProviderForSummary(updatedSummary).getProviderStore().addItemSummary(updatedSummary);
            }
        }
    }

    private void deleteItem(final IItemSummary<O> oldSummary)
    {
        final SummaryGroup<O> summaryGroup = oldSummary.getSummaryGroup();
        addDeferredAction(summaryGroup, new Action<O>(Operations.DELETE, oldSummary, new Runnable() {
            @Override
            public void run()
            {
                IProvider<O> inProvider = getProviderForSummary(oldSummary);
                for (IProvider<O> provider : providers.values())
                {
                    if (provider != inProvider)
                    {
                        String foreignID = summaryGroup.getSummaryForProvider(provider).getIdentifier();
                        provider.deleteObject(foreignID);
                        provider.getProviderStore().deleteItemSummary(foreignID);
                    }
                }
                inProvider.getProviderStore().deleteItemSummary(oldSummary.getIdentifier());
            }
        }));
    }

    private void addDeferredAction(SummaryGroup<O> summaryGroup, Action<O> action)
    {
        Action<O> oldAction = deferredActions.get(summaryGroup);
        if (oldAction != null)
        {
            final Conflict<O> c = new Conflict<O>(oldAction);
            c.setTask(new Runnable() {

                @Override
                public void run()
                {
                    listener.onConflict(c);
                }
            });
            c.addConfictAction(action);
            action = c;
        }
        deferredActions.put(summaryGroup, action);
    }

    private void updateItem(final IItemSummary<O> oldSummary, final IItemSummary<O> newSummary)
    {
        final SummaryGroup<O> summaryGroup = oldSummary.getSummaryGroup();
        addDeferredAction(summaryGroup, new Action<O>(Operations.UPDATE, newSummary, new Runnable() {
            @Override
            public void run()
            {
                IProvider<O> inProvider = getProviderForSummary(oldSummary);
                final O fetchObject = inProvider.fetchObject(oldSummary.getIdentifier());
                for (IProvider<O> provider : providers.values())
                {
                    if (provider != inProvider)
                    {
                        String foreignID = summaryGroup.getSummaryForProvider(provider).getIdentifier();
                        IItemSummary<O> updatedSummary = provider.updateObject(foreignID, fetchObject);
                        // Replace existing
                        provider.getProviderStore().addItemSummary(updatedSummary);
                        summaryGroup.addSummary(updatedSummary);
                    }
                }
                getProviderForSummary(newSummary).getProviderStore().addItemSummary(newSummary);
                summaryGroup.addSummary(newSummary);
            }
        }));
    }

    private void addItem(final IItemSummary<O> summary)
    {
        // Get existing summary group if there already is one, or create a new
        // one.
        final SummaryGroup<O> summaryGroup = summary.getSummaryGroup() == null
                ? new SummaryGroup<O>()
                : summary.getSummaryGroup();
        final IProvider<O> inProvider = getProviderForSummary(summary);
        final O fetchObject = inProvider.fetchObject(summary.getIdentifier());

        boolean equivConflict = false;

        // Loop through all providers.
        for (IProvider<O> provider : providers.values())
        {
            IItemSummary<O> summaryForProvider = summaryGroup.getSummaryForProvider(provider);
            if (summaryForProvider != null)
            {
                // There is already an equivalent record. Check if it's
                // different. We only need to add the conflict once.
                if (summary.getHash().equals(summaryForProvider.getHash()))
                {
                    if (!equivConflict)
                    {
                        // Just add one action, the other side of the conflict
                        // will do the same.
                        addDeferredAction(summaryGroup, new Action<O>(Operations.INSERT, summary, new Runnable() {

                            @Override
                            public void run()
                            {
                                for (IProvider<O> provider : providers.values())
                                {
                                    if (provider != inProvider)
                                    {
                                        String foreignID = summaryGroup.getSummaryForProvider(provider).getIdentifier();
                                        IItemSummary<O> updatedSummary = provider.updateObject(foreignID, fetchObject);
                                        // Replace existing
                                        provider.getProviderStore().addItemSummary(updatedSummary);
                                        summaryGroup.addSummary(updatedSummary);
                                    }
                                }
                                inProvider.getProviderStore().addItemSummary(summary);
                                summaryGroup.addSummary(summary);
                            }
                        }));
                        // Don't do this again.
                        equivConflict = true;
                    }
                }
            }
            else if (provider != inProvider)
            {
                IItemSummary<O> updatedSummary = provider.insertObject(fetchObject);
                provider.getProviderStore().addItemSummary(updatedSummary);
                summaryGroup.addSummary(updatedSummary);
            }
        }
        // Only commit the result now if there was no conflict.
        if (!equivConflict)
        {
            inProvider.getProviderStore().addItemSummary(summary);
            summaryGroup.addSummary(summary);
        }
    }

    @Override
    public void addConflictListener(IConflictListener<O> listener)
    {
        this.listener = listener;
    }

    public SyncState<O> getSyncState()
    {
        return syncState;
    }

    public IProvider<O> getProviderForSummary(IItemSummary<O> summary)
    {
        return providers.get(summary.getProviderName());
    }
}

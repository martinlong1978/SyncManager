package com.martinutils.sync.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    private final Set<IProvider<O>> providers = new HashSet<IProvider<O>>();

    private final Map<SummaryGroup<O>, Action<O>> deferredActions = new HashMap<SummaryGroup<O>, Action<O>>();

    private IConflictListener<O> listener;

    @Override
    public void addProvider(IProvider<O> provider)
    {
        providers.add(provider);
        provider.setProviderStore(new ProviderStore<O>());
    }

    @Override
    public void syncProviders()
    {
        // Loop through each provider.
        for (IProvider<O> provider : providers)
        {
            processProvider(provider);
        }
    }

    private void processProvider(IProvider<O> provider)
    {
        IProviderStore<O> store = provider.getProviderStore();
        store.reset();
        for (IItemSummary<O> summary : provider.getSummaries())
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
        for (IProvider<O> provider : providers)
        {
            final IItemSummary<O> providerSummary = storedSummary.getSummaryGroup().getSummaryForProvider(provider);
            if (providerSummary == null)
            {
                final O fetchObject = storedSummary.getProvider().fetchObject(storedSummary.getIdentifier());
                SummaryGroup<O> summaryGroup = storedSummary.getSummaryGroup();
                IItemSummary<O> newSummary = provider.insertObject(fetchObject);
                summaryGroup.addSummary(newSummary);
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
                for (IProvider<O> provider : providers)
                {
                    if (provider != oldSummary.getProvider())
                    {
                        String foreignID = summaryGroup.getSummaryForProvider(provider).getIdentifier();
                        provider.deleteObject(foreignID);
                        provider.getProviderStore().deleteItemSummary(foreignID);
                    }
                }
                oldSummary.getProvider().getProviderStore().deleteItemSummary(oldSummary.getIdentifier());
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
                final O fetchObject = oldSummary.getProvider().fetchObject(oldSummary.getIdentifier());
                for (IProvider<O> provider : providers)
                {
                    if (provider != oldSummary.getProvider())
                    {
                        String foreignID = summaryGroup.getSummaryForProvider(provider).getIdentifier();
                        IItemSummary<O> updatedSummary = provider.updateObject(foreignID, fetchObject);
                        // Replace existing
                        provider.getProviderStore().addItemSummary(updatedSummary);
                        summaryGroup.addSummary(updatedSummary);
                    }
                }
                summaryGroup.addSummary(newSummary);
            }
        }));
    }

    private void addItem(final IItemSummary<O> summary)
    {
        final SummaryGroup<O> summaryGroup = new SummaryGroup<O>();
        addDeferredAction(summaryGroup, new Action<O>(Operations.INSERT, summary, new Runnable() {
            @Override
            public void run()
            {
                final O fetchObject = summary.getProvider().fetchObject(summary.getIdentifier());
                // Loop through each provider.
                for (IProvider<O> provider : providers)
                {
                    if (provider != summary.getProvider())
                    {
                        IItemSummary<O> updatedSummary = provider.insertObject(fetchObject);
                        provider.getProviderStore().addItemSummary(updatedSummary);
                        summaryGroup.addSummary(updatedSummary);
                    }
                }
                summary.getProvider().getProviderStore().addItemSummary(summary);
                summaryGroup.addSummary(summary);
            }
        }));
    }

    @Override
    public void addConflictListener(IConflictListener<O> listener)
    {
        this.listener = listener;
    }
}

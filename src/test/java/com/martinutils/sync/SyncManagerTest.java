package com.martinutils.sync;

import mockit.Delegate;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;

import org.junit.Test;

import com.martinutils.sync.impl.SyncManager;

public class SyncManagerTest
{

    @Mocked
    IProvider<SyncClass> provA;
    @Mocked
    IProvider<SyncClass> provB;
    @Mocked
    IConflictListener<SyncClass> conflict;

    static class ProviderDelegate implements Delegate<IProvider<SyncClass>>
    {
        IProviderStore<SyncClass> capturedStore;

        void setProviderStore(IProviderStore<SyncClass> store)
        {
            this.capturedStore = store;
        }

        IProviderStore<SyncClass> getProviderStore()
        {
            return this.capturedStore;
        }

    }

    @Test
    public void test()
    {

        final TestItemSummary summary1A = new TestItemSummary(null, "ProvA", "1234", "1234", null);
        final TestItemSummary summary1B = new TestItemSummary(null, "ProvB", "1238", "1235", null);

        final ProviderDelegate pDelA = new ProviderDelegate();
        final ProviderDelegate pDelB = new ProviderDelegate();

        new NonStrictExpectations() {

            {
                // Get the names
                provA.getName();
                result = "ProvA";
                provA.setProviderStore((IProviderStore<SyncClass>) any);
                result = pDelA;

                provB.getName();
                result = "ProvB";
                provB.setProviderStore((IProviderStore<SyncClass>) any);
                result = pDelB;

                // Get the summaries
                provA.getSummaries();
                result = new TestItemSummary[] { summary1A };

                provB.getSummaries();
                result = new TestItemSummary[] {};

                provA.getProviderStore();
                result = pDelA;

                provA.fetchObject("1234");
                result = new SyncClass();

                provB.insertObject((SyncClass) any);
                result = summary1B;

                provB.getProviderStore();
                result = pDelB;

            }
        };

        SyncManager<SyncClass> manager = new SyncManager<SyncClass>();
        manager.addProvider(provA);
        manager.addProvider(provB);
        manager.addConflictListener(conflict);
        manager.syncProviders();

        new Verifications() {
            {
                provA.fetchObject("1234");

                provB.insertObject((SyncClass) any);
            }
        };
    }
}

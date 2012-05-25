package com.martinutils.sync;

import com.martinutils.sync.impl.SummaryGroup;

class TestItemSummary implements IItemSummary<SyncClass>
{
    private SummaryGroup<SyncClass> group;
    private final String providerName;
    private final String id;
    private final String hash;
    private final String globalID;

    public TestItemSummary(SummaryGroup<SyncClass> group, String provider, String id, String hash, String globalID)
    {
        super();
        this.group = group;
        this.providerName = provider;
        this.id = id;
        this.hash = hash;
        this.globalID = globalID;
    }

    @Override
    public void setSummaryGroup(SummaryGroup<SyncClass> group)
    {
        this.group = group;
    }

    @Override
    public SummaryGroup<SyncClass> getSummaryGroup()
    {
        return group;
    }

    @Override
    public String getIdentifier()
    {
        return id;
    }

    @Override
    public String getHash()
    {
        return hash;
    }

    @Override
    public String getGlobalID()
    {
        return globalID;
    }

    @Override
    public String getProviderName()
    {
        return providerName;
    }

    @Override
    public String toString()
    {
        return providerName + "|" + id + "|" + hash + "|" + globalID + "|";
    }

}

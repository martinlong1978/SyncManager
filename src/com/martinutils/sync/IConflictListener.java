package com.martinutils.sync;

import com.martinutils.sync.operations.Conflict;

public interface IConflictListener<O>
{

    void onConflict(Conflict<O> conflict);

}

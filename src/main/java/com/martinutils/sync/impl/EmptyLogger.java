package com.martinutils.sync.impl;

import com.martinutils.sync.ILogger;

public class EmptyLogger implements ILogger
{

    @Override
    public void v(String message)
    {
    }

    @Override
    public void d(String message)
    {
    }

    @Override
    public void i(String message)
    {
    }

    @Override
    public void w(String message)
    {
    }

    @Override
    public void e(String message)
    {
    }

    @Override
    public void e(Throwable exception)
    {
    }

    @Override
    public void s(String message)
    {
    }

    @Override
    public void f(String message)
    {
    }

}

package com.martinutils.sync;

public interface ILogger
{

    public void v(String message);

    public void d(String message);

    public void i(String message);

    public void w(String message);

    public void e(String message);

    public void e(Throwable exception);

    public void s(String message);

    public void f(String message);

}

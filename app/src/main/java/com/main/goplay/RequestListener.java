package com.main.goplay;

public abstract class RequestListener {
    public abstract void waiterForBool(boolean bool);
    public abstract void waiterForLink(String link);
    public abstract void rejection();
}


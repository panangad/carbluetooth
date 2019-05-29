package com.xpg.manager;

import android.content.Context;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public interface IDriver extends Serializable {
    Map<String, Object> connect(String str);

    void disConnect();

    void finding();

    Set<?> getExistedDevice();

    boolean isOpen();

    void register(boolean z);

    void request();

    void setContext(Context context);
}

package com.xpg.manager;

import java.util.Map;

public interface MobileControllerDelegate {
    void connectionWasDisconnected();

    void connectionWasSetup(boolean z);

    void receiveData(Map<String, Object> map);
}

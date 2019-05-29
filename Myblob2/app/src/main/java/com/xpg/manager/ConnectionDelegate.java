package com.xpg.manager;

import java.util.Map;

public interface ConnectionDelegate {
    void connectionWasFailed(String str);

    void connectionWasSetup(String str, Map<String, Object> map);
}

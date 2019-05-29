package com.xpg.convertor;

import android.content.Context;

import com.example.manuelj.myblob2.R;
import com.xpg.constant.Constants;
import com.xpg.util.ByteUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MobileConvertor {
    private static MobileConvertor defaultConverter;
    Context context;
    ProtocolConvertor convertor;

    private MobileConvertor() {
    }

    public static MobileConvertor defaultConverter() {
        if (defaultConverter == null) {
            defaultConverter = new MobileConvertor();
        }
        return defaultConverter;
    }

    public void setContext(Context context, String currentModel) {
        this.context = context;
        this.convertor = new ProtocolConvertor().initConfig(context.getResources().openRawResource(R.raw.config), currentModel);
    }

    public void setContext(Context context, int protocalFileResources, String currentModel) {
        this.context = context;
        this.convertor = new ProtocolConvertor().initConfig(context.getResources().openRawResource(protocalFileResources), ProtocolConstant.NAME, currentModel);
    }

    public byte[] convertFromDataToProtocol(Map<String, Float> remoteDataMap) {
        int[] sendInts = null;
        Iterator<?> it = remoteDataMap.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            sendInts = this.convertor.convertToInts(key, ((Float) remoteDataMap.get(key)).floatValue(), sendInts, false);
        }
        int[] ints = new int[this.convertor.byteTotal];
        int i;
        if (this.convertor.reverse == 1) {
            for (i = 0; i < sendInts.length; i++) {
                ints[i] = (byte) sendInts[sendInts.length - (i + 1)];
            }
        } else {
            for (i = 0; i < sendInts.length; i++) {
                ints[i] = (byte) sendInts[i];
            }
        }
        return this.convertor.stringToBytes(new StringBuilder(String.valueOf(this.convertor.header)).append(this.convertor.intsToString(ints)).toString());
    }

    public Map<String, Float> convertFromProtocolToData(byte[] receiveData) {
        int i;
        String s = Constants.CURRENT_MODEL;
        int[] ints = ByteUtil.removeHeader0X(ByteUtil.bytesToInts(receiveData));
        for (int append : ints) {
            s = new StringBuilder(String.valueOf(s)).append(append).append(" - ").toString();
        }
        Map<String, Float> receiveDataMap = new HashMap();
        for (i = 0; i < ints.length; i++) {
            receiveDataMap = this.convertor.convertReceiveData(ints[i], i, receiveDataMap);
        }
        return receiveDataMap;
    }

    public void recycleContext() {
        this.context = null;
    }
}

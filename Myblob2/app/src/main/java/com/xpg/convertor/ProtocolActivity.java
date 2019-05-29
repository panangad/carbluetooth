package com.xpg.convertor;

import android.app.Activity;
import android.os.Bundle;

import com.xpg.C0085R;

import Constant.XPGConstant;

public class ProtocolActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BF10();
    }

    public void silverlit() {
        ProtocolConvertor convertor = new ProtocolConvertor().initConfig(getResources().openRawResource(C0085R.raw.config), "Silverlit Helicopter");
        byte[] send = convertor.stringToBytes("x" + convertor.intsToString(convertor.convertToInts(ProtocolConstant.MATCH_KEY, 1.0f, convertor.convertToInts(ProtocolConstant.LIGHT_KEY, 3.0f, convertor.convertToInts(ProtocolConstant.TRIMER_KEY, 50.0f, convertor.convertToInts(ProtocolConstant.YAW_KEY, 50.0f, convertor.convertToInts(ProtocolConstant.PITCH_KEY, 50.0f, convertor.convertToInts(ProtocolConstant.ROTOR_KEY, 50.0f, null, true), true), true), true), false), false)));
    }

    public void BF10() {
        ProtocolConvertor convertor = new ProtocolConvertor().initConfig(getResources().openRawResource(C0085R.raw.config), "XXX Helicopter");
        ProtocolConvertor protocolConvertor = convertor;
        byte[] sendBytes = protocolConvertor.convertToBytes(ProtocolConstant.PITCH_KEY, 96.0f, convertor.convertToBytes(ProtocolConstant.ROTOR_KEY, 50.0f, null, true, false), true, true);
        sendBytes = convertor.convertToBytes(ProtocolConstant.YAW_KEY, 55.0f, sendBytes, true, true);
        sendBytes = convertor.convertToBytes(ProtocolConstant.BACKUP_KEY, XPGConstant.RECTF_LEFT_LEFT, convertor.convertToBytes(ProtocolConstant.CONTROL_KEY, XPGConstant.RECTF_LEFT_LEFT, convertor.convertToBytes(ProtocolConstant.TAIL_KEY, 187.0f, convertor.convertToBytes(ProtocolConstant.HEAD_KEY, 170.0f, convertor.convertToBytes(ProtocolConstant.ID_KEY, 1.0f, convertor.convertToBytes(ProtocolConstant.TRIMER_KEY, 20.0f, sendBytes, true, true), false), false), false), false), false);
    }
}

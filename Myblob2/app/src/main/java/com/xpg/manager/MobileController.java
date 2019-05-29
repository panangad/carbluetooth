package com.xpg.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xpg.convertor.MobileConvertor;
import com.xpg.util.ByteUtil;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class MobileController {
    private static final int CONNECTION_DISCONNECTED = 998;
    private static final int RECEIVE_DATA = 999;
    private static final int RECEIVE_FAILED = 996;
    private static final int SEND_FAILED = 997;
    private static MobileController defaultController;
    private MobileConvertor converter = MobileConvertor.defaultConverter();
    private MobileControllerDelegate delegate;
    public DataOutputStream dos;
    private Handler handler = new C00881();
    public InputStream remoteInputStream;
    public OutputStream remoteOutputStream;
    private boolean startReceiveData = false;

    /* renamed from: com.xpg.manager.MobileController$1 */
    class C00881 extends Handler {
        C00881() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MobileController.SEND_FAILED /*997*/:
                case MobileController.CONNECTION_DISCONNECTED /*998*/:
                    if (MobileController.this.delegate != null) {
                        MobileController.this.delegate.connectionWasDisconnected();
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: com.xpg.manager.MobileController$2 */
    class C00892 extends Handler {
        C00892() {
        }

        public void handleMessage(Message msg) {
            if (msg.what == MobileController.RECEIVE_DATA) {
                byte[] data = (byte[]) msg.obj;
                try {
                    Map<String, Object> receiveData = new HashMap();
                    float percent = (((float) ByteUtil.toTenFromHexString(ByteUtil.showByteContent(data).substring(2, 4))) * 100.0f) / 255.0f;
                    int strike_int1 = ByteUtil.toTenFromHexString(ByteUtil.showByteContent(data).substring(4, 6));
                    int strike_int2 = ByteUtil.toTenFromHexString(ByteUtil.showByteContent(data).substring(6, 8));
                    Boolean strike = Boolean.valueOf(false);
                    if (strike_int1 == 1 || strike_int2 == 1) {
                        strike = Boolean.valueOf(true);
                    }
                    receiveData.put("Battery", Float.valueOf(percent));
                    receiveData.put("Strike", strike);
                    if (MobileController.this.delegate != null) {
                        MobileController.this.delegate.receiveData(receiveData);
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private MobileController() {
    }

    public static MobileController defaultController() {
        if (defaultController == null) {
            defaultController = new MobileController();
        }
        return defaultController;
    }

    public void updateManager(Context context, int protocolFlie, String deviceModel) {
        this.converter.setContext(context, protocolFlie, deviceModel);
    }

    public void setDelegate(MobileControllerDelegate delegate) {
        this.delegate = delegate;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.remoteOutputStream = outputStream;
        if (this.remoteOutputStream != null) {
            this.dos = new DataOutputStream(this.remoteOutputStream);
        }
    }

    public void setInputStream(InputStream inputStream) {
        this.remoteInputStream = inputStream;
    }

    public void clearStreams() {
        if (this.remoteInputStream != null) {
            try {
                this.remoteInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.remoteInputStream = null;
        }
        if (this.remoteOutputStream != null) {
            try {
                this.remoteOutputStream.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            this.remoteOutputStream = null;
        }
    }

    public void sendData(Map<String, Float> remoteDataMap) {
        if (this.remoteOutputStream != null) {
            try {
                //remoteDataMap.put(ProtocolConstant.LIGHT_KEY, Float.valueOf((float) 56));
                byte[] tmp = this.converter.convertFromDataToProtocol(remoteDataMap);
                String result = String.valueOf('-');
                for (byte x:
                     tmp) {
                    result += String.valueOf(x)+"-";

                }
                Log.e("EEEE", result );
                this.remoteOutputStream.write(tmp);
            } catch (IOException e) {
                this.remoteOutputStream = null;
                e.printStackTrace();
                Message message = new Message();
                message.what = SEND_FAILED;
                this.handler.sendMessage(message);
                Log.i("sendData", e.toString());
            }
        }
    }

    public void startReceiveData() {
        this.startReceiveData = true;
        if (this.remoteInputStream != null) {
            final Handler hander = new C00892();
            new Thread() {
                public void run() {
                    Message message;
                    while (MobileController.this.startReceiveData) {
                        byte[] data = new byte[1024];
                        try {
                            if (MobileController.this.remoteInputStream != null) {
                                data = ByteUtil.copyBytes(data, MobileController.this.remoteInputStream.read(data));
                                Log.i("receiveData", "after read:" + ByteUtil.showByteContent(data));
                                message = new Message();
                                message.what = MobileController.RECEIVE_DATA;
                                message.obj = data;
                                hander.sendMessage(message);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            message = new Message();
                            e.printStackTrace();
                            if ("Try again".equals(e.getMessage())) {
                                message.what = MobileController.RECEIVE_FAILED;
                            } else {
                                MobileController.this.startReceiveData = false;
                                MobileController.this.remoteInputStream = null;
                                message.what = MobileController.CONNECTION_DISCONNECTED;
                            }
                            MobileController.this.handler.sendMessage(message);
                        }
                    }
                }
            }.start();
        }
    }

    public void stopReceiveData() {
        this.startReceiveData = false;
    }
}

package com.xpg;

import android.app.Activity;
import android.os.Bundle;

import com.xpg.convertor.MobileConvertor;
import com.xpg.manager.AccelerometerManager;
import com.xpg.manager.AccelerometerManagerDelegate;
import com.xpg.manager.MobileController;
import com.xpg.manager.MobileControllerDelegate;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends Activity implements AccelerometerManagerDelegate, MobileControllerDelegate {
    public static final int CUSTOM_GESTURE_MODE = 4;
    public static final int FULL_GESTURE_MODE = 3;
    public static final int HALF_GESTURE_MODE = 1;
    public static final int JOYSTICK_MODE = 2;
    private int controlMode = 2;
    private boolean flag = false;
    public DefinitionModel heliModel;
    private TimerTask task;
    private Timer timer;

    /* renamed from: com.xpg.GameActivity$1 */
    class C00841 extends TimerTask {
        C00841() {
        }

        public void run() {
            while (GameActivity.this.flag) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                GameActivity.this.heliModel.sendRemoteData();
            }
        }
    }

    public void onCreate(Bundle savedInstanceState, int controlMode, String currentModel) {
        super.onCreate(savedInstanceState);
        this.controlMode = controlMode;
        MobileConvertor.defaultConverter().setContext(this, currentModel);
        MobileController.defaultController().setDelegate(this);
        if (this.controlMode != 2) {
            AccelerometerManager.defalutManager().setDelegate(this, this);
        }
    }

    public void setModel(DefinitionModel deviceModel) {
        this.heliModel = deviceModel;
    }

    public void onDestroy() {
        if (!(this.controlMode == 2 || this.controlMode == 4)) {
            openAccelerometer(false);
            AccelerometerManager.defalutManager().setDelegate(null);
        }
        MobileConvertor.defaultConverter().recycleContext();
        super.onDestroy();
    }

    protected void onStop() {
        super.onStop();
    }

    public void openAccelerometer(boolean open) {
        if (open) {
            AccelerometerManager.defalutManager().startUpdate();
        } else {
            AccelerometerManager.defalutManager().stopUpdate();
        }
    }

    public void startSend() {
        MobileController.defaultController().startReceiveData();
        this.heliModel.reset();
        if (this.task == null) {
            this.flag = true;
            this.task = new C00841();
            this.timer = new Timer();
            this.timer.schedule(this.task, 0);
        }
    }

    public void stopSend() {
        this.heliModel.sendResetRemoteData();
        MobileController.defaultController().stopReceiveData();
        this.heliModel.reset();
        try {
            if (this.timer != null && this.task != null) {
                this.timer.cancel();
                this.flag = false;
                this.timer = null;
                this.task = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateAccelerometer(float x, float y, float z) {
        this.heliModel.filterPitchByY(y);
        this.heliModel.filterYawByX(x);
    }

    public void updateAcceration(float xAcc, float yAcc, float zAcc) {
    }

    public void receiveData(Map<String, Object> map) {
    }

    public void connectionWasSetup(boolean isSuccess) {
    }

    public void connectionWasDisconnected() {
    }
}

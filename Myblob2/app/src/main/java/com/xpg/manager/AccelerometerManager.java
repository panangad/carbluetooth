package com.xpg.manager;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AccelerometerManager {
    private static AccelerometerManager defaultManager = null;
    private static final float kFilteringFactor = 1.0f;
    private static Sensor sensor;
    private static SensorManager sensorManager;
    private AccelerometerManagerDelegate delegate;
    private boolean isOpen = false;
    private SensorEventListener lsn = new C00861();

    /* renamed from: com.xpg.manager.AccelerometerManager$1 */
    class C00861 implements SensorEventListener {
        C00861() {
        }

        public void onSensorChanged(SensorEvent e) {
            if (AccelerometerManager.this.isOpen && e.sensor == AccelerometerManager.sensor) {
                float accelX = e.values[0];
                float accelY = e.values[1];
                float accelZ = e.values[2];
                float accelX1 = (float) (((double) (accelX * 1.0f)) + (((double) accelX) * 0.0d));
                float accelY1 = (float) (((double) (accelY * 1.0f)) + (((double) accelY) * 0.0d));
                float accelZ1 = (float) (((double) (accelZ * 1.0f)) + (((double) accelZ) * 0.0d));
                if (AccelerometerManager.this.delegate != null) {
                    AccelerometerManager.this.delegate.updateAccelerometer(accelX1, accelY1, accelZ1);
                }
            }
        }

        public void onAccuracyChanged(Sensor s, int accuracy) {
        }
    }

    private AccelerometerManager() {
    }

    public static AccelerometerManager defalutManager() {
        if (defaultManager == null) {
            defaultManager = new AccelerometerManager();
        }
        return defaultManager;
    }

    public AccelerometerManager setDelegate(Activity activity, AccelerometerManagerDelegate delegate) {
        this.delegate = delegate;
        if (sensorManager == null) {
            sensorManager = (SensorManager) activity.getSystemService("sensor");
        }
        sensor = sensorManager.getDefaultSensor(1);
        return defaultManager;
    }

    public void setDelegate(AccelerometerManagerDelegate delegate) {
        this.delegate = null;
    }

    public void startUpdate() {
        this.isOpen = true;
        sensorManager.registerListener(this.lsn, sensor, 0);
    }

    public void stopUpdate() {
        this.isOpen = false;
        sensorManager.unregisterListener(this.lsn, sensor);
    }
}

package com.xpg;

import com.xpg.constant.Constants;
import com.xpg.convertor.ProtocolConstant;
import com.xpg.manager.MobileController;
import com.xpg.util.MathUtil;

import java.util.HashMap;
import java.util.Map;

public class DefinitionModelAdapter implements DefinitionModel {
    public boolean isOpen = true;
    public float pitch;
    public float rotor;
    public float trimer;
    public float yaw;

    public void filterPitchByY(float y) {
        this.pitch = ((float) Constants.MAX_PITCH) - (((y + 9.0f) / 18.0f) * ((float) (Constants.MAX_PITCH - Constants.MIN_PITCH)));
        if (this.pitch < ((float) Constants.MIN_PITCH)) {
            this.pitch = (float) Constants.MIN_PITCH;
        }
        if (this.pitch > ((float) Constants.MAX_PITCH)) {
            this.pitch = (float) Constants.MAX_PITCH;
        }
    }

    public void filterPitch(float pitch) {
        this.pitch = (pitch + 100.0f) / 2.0f;
        if (this.pitch < ((float) Constants.MIN_PITCH)) {
            this.pitch = (float) Constants.MIN_PITCH;
        }
        if (this.pitch > ((float) Constants.MAX_PITCH)) {
            this.pitch = (float) Constants.MAX_PITCH;
        }
    }

    public void filterYawByX(float x) {
        this.yaw = ((x + 9.0f) / 18.0f) * ((float) (Constants.MAX_YAW - Constants.MIN_YAW));
        if (this.yaw < ((float) Constants.MIN_YAW)) {
            this.yaw = (float) Constants.MIN_YAW;
        }
        if (this.yaw > ((float) Constants.MAX_YAW)) {
            this.yaw = (float) Constants.MAX_YAW;
        }
    }

    public void filterYaw(float yaw) {
        this.yaw = ((yaw + 90.0f) / 180.0f) * 100.0f;
        if (this.yaw < ((float) Constants.MIN_YAW)) {
            this.yaw = (float) Constants.MIN_YAW;
        }
        if (this.yaw > ((float) Constants.MAX_YAW)) {
            this.yaw = (float) Constants.MAX_YAW;
        }
        this.yaw = MathUtil.to255(this.yaw, Constants.MAX_YAW);
    }

    public void filterRotor(float rotor) {
        this.rotor = rotor;
        if (this.rotor < ((float) Constants.MIN_ROTOR)) {
            this.rotor = (float) Constants.MIN_ROTOR;
        }
        if (this.rotor > ((float) Constants.MAX_ROTOR)) {
            this.rotor = (float) Constants.MAX_ROTOR;
        }
        this.rotor = MathUtil.to255(this.rotor, Constants.MAX_ROTOR);
    }

    public void filterTrimer(float trimer) {
        this.trimer = trimer;
        this.trimer = ((float) Constants.MAX_TRIMER) - this.trimer;
        if (this.trimer < ((float) Constants.MIN_TRIMER)) {
            this.trimer = (float) Constants.MIN_TRIMER;
        }
        if (this.trimer > ((float) Constants.MAX_TRIMER)) {
            this.trimer = (float) Constants.MAX_TRIMER;
        }
    }

    public void reset() {
        resetRotor();
        resetDirection();
        this.trimer = (float) ((Constants.MAX_TRIMER - (Constants.MIN_TRIMER < 0 ? -Constants.MIN_TRIMER : Constants.MIN_TRIMER)) / 2);
    }

    public void resetRotor() {
        this.rotor = (float) Constants.MIN_ROTOR;
    }

    public void resetDirection() {
        this.pitch = (float) ((Constants.MAX_PITCH - (Constants.MIN_PITCH < 0 ? -Constants.MIN_PITCH : Constants.MIN_PITCH)) / 2);
        this.yaw = (float) ((Constants.MAX_YAW - (Constants.MIN_YAW < 0 ? -Constants.MIN_YAW : Constants.MIN_YAW)) / 2);
    }

    public void sendResetRemoteData() {
        reset();
        Map<String, Float> remoteDataMap = new HashMap();
        remoteDataMap.put(ProtocolConstant.ROTOR_KEY, Float.valueOf(this.rotor));
        remoteDataMap.put(ProtocolConstant.PITCH_KEY, Float.valueOf(this.pitch));
        remoteDataMap.put(ProtocolConstant.YAW_KEY, Float.valueOf(this.yaw));
        remoteDataMap.put(ProtocolConstant.TRIMER_KEY, Float.valueOf(this.trimer));
        MobileController.defaultController().sendData(remoteDataMap);
    }

    public void sendRemoteData() {
        if (this.isOpen) {
            Map<String, Float> remoteDataMap = new HashMap();
            remoteDataMap.put(ProtocolConstant.ROTOR_KEY, Float.valueOf(this.rotor));
            remoteDataMap.put(ProtocolConstant.PITCH_KEY, Float.valueOf(this.pitch));
            remoteDataMap.put(ProtocolConstant.YAW_KEY, Float.valueOf(this.yaw));
            remoteDataMap.put(ProtocolConstant.TRIMER_KEY, Float.valueOf(this.trimer));
            MobileController.defaultController().sendData(remoteDataMap);
        }
    }

    public void pauseSendRemoteData() {
        this.isOpen = false;
    }

    public void resumeSendRemoteData() {
        this.isOpen = true;
    }

    public void addRotorCompensation() {
    }

    public int monitorState(float accelX, float accelY) {
        return 0;
    }

    public int monitorBF(float tempY) {
        return 0;
    }

    public int changeLightState() {
        return 0;
    }

    public void isBusyable(boolean busyNow) {
    }
}

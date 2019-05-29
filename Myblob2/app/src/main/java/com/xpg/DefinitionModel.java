package com.xpg;

public interface DefinitionModel {
    void addRotorCompensation();

    int changeLightState();

    void filterPitch(float f);

    void filterPitchByY(float f);

    void filterRotor(float f);

    void filterTrimer(float f);

    void filterYaw(float f);

    void filterYawByX(float f);

    void isBusyable(boolean z);

    int monitorBF(float f);

    int monitorState(float f, float f2);

    void pauseSendRemoteData();

    void reset();

    void resetDirection();

    void resetRotor();

    void resumeSendRemoteData();

    void sendRemoteData();

    void sendResetRemoteData();
}

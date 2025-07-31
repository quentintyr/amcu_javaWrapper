package com.frc.amcu;

public interface LimitSwitchCallback {
    void onLimitSwitchTriggered(int motorNr, int high);
}

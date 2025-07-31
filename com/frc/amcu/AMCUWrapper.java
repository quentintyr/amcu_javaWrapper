package com.frc.amcu;

import java.util.concurrent.*;

/**
 * Java wrapper for the AMCU (Advanced Motor Control Unit) C++ library.
 * This class provides a Java interface to control motors and drive bases
 * for FRC 2020 robotics applications.
 */
public class AMCUWrapper {

    private static boolean nativeLoaded = false;
    private static String loadingError = null;
    static {
        try {
            System.loadLibrary("wpiutil");
            System.loadLibrary("wpilibc");
            System.loadLibrary("wpiHAL");
            System.loadLibrary("ntcore");
            System.load("/home/lvuser/libamcuJni.so");
            nativeLoaded = true;
        } catch (UnsatisfiedLinkError e) {
            loadingError = "Failed to load native library: " + e.getMessage();
            nativeLoaded = false;
        }
    }

    // Native pointer to the C++ AMCU instance
    private long nativePtr;

    // Callback storage
    private static LimitSwitchCallback limitSwitchCallback;
    private static DriveActionCallback driveActionCallback;

    /**
     * Constructor - creates a new AMCU instance
     */
    public AMCUWrapper() {
        System.out.println("AMCUWrapper: Constructor called");
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("linux") && nativeLoaded) {
            try {
                System.out.println("AMCUWrapper: Creating native instance...");
                nativePtr = createNative();
                System.out.println("AMCUWrapper: Native instance created successfully");
            } catch (Exception e) {
                System.out.println("AMCUWrapper: Failed to create native instance: " + e.getMessage());
                nativePtr = 0;
            }
        } else {
            System.out.println("AMCUWrapper: Running in development mode (no native library)");
            nativePtr = 0;
        }
    }

    /**
     * Check if native library is loaded and available
     * 
     * @return true if native library is loaded, false if in development mode
     */
    public boolean isNativeLoaded() {
        return nativeLoaded && nativePtr != 0;
    }

    /**
     * Get any loading error that occurred
     */
    public String getLoadingError() {
        return loadingError;
    }

    private void checkNativeLoaded() {
        if (!isNativeLoaded()) {
            String error = "Native library not available. ";
            if (loadingError != null) {
                error += "Error: " + loadingError;
            } else {
                error += "Running in development mode on " + System.getProperty("os.name");
            }
            throw new UnsupportedOperationException(error);
        }
    }

    /**
     * Cleanup method - should be called when done with the AMCU instance
     */
    public void close() {
        if (nativeLoaded && nativePtr != 0) {
            destroyNative(nativePtr);
            nativePtr = 0;
        }
    }

    /**
     * Initializes the omnidirectional drive base
     * 
     * @param wheelRadiusMm wheel radius in mm
     * @param robotRadiusMm robot radius in mm
     * @param motorLeft     number of the left motor (0-3)
     * @param motorRight    number of the right motor (0-3)
     * @param motorBack     number of the back motor (0-3)
     */
    public void initOmniDriveBase(int wheelRadiusMm, int robotRadiusMm,
            int motorLeft, int motorRight, int motorBack) {
        checkNativeLoaded();
        initOmniDriveBaseNative(nativePtr, wheelRadiusMm, robotRadiusMm,
                motorLeft, motorRight, motorBack);
    }

    /**
     * Initializes the mecanum drive base
     * 
     * @param wheelRadiusMm   wheel radius in mm
     * @param robotXMm        robot length in mm
     * @param robotYMm        robot width in mm
     * @param motorFrontLeft  number of the front left motor (0-3)
     * @param motorFrontRight number of the front right motor (0-3)
     * @param motorBackLeft   number of the back left motor (0-3)
     * @param motorBackRight  number of the back right motor (0-3)
     */
    public void initMecanumDriveBase(int wheelRadiusMm, int robotXMm, int robotYMm,
            int motorFrontLeft, int motorFrontRight,
            int motorBackLeft, int motorBackRight) {
        checkNativeLoaded();
        initMecanumDriveBaseNative(nativePtr, wheelRadiusMm, robotXMm, robotYMm,
                motorFrontLeft, motorFrontRight,
                motorBackLeft, motorBackRight);
    }

    /**
     * Initializes the 2 wheeled differential drive
     * 
     * @param wheelRadiusMm   wheel radius in mm
     * @param wheelDistanceMm distance between wheels in mm
     * @param motorLeft       number of the left motor (0-3)
     * @param motorRight      number of the right motor (0-3)
     */
    public void initDifferentialDriveBase2Wheel(int wheelRadiusMm, int wheelDistanceMm,
            int motorLeft, int motorRight) {
        checkNativeLoaded();
        initDifferentialDriveBase2WheelNative(nativePtr, wheelRadiusMm, wheelDistanceMm,
                motorLeft, motorRight);
    }

    /**
     * Initializes the 4 wheeled differential drive
     * 
     * @param wheelRadiusMm   wheel radius in mm
     * @param wheelDistanceMm wheel distance in mm
     * @param motorFrontLeft  number of the front left motor (0-3)
     * @param motorFrontRight number of the front right motor (0-3)
     * @param motorBackLeft   number of the back left motor (0-3)
     * @param motorBackRight  number of the back right motor (0-3)
     */
    public void initDifferentialDriveBase4Wheel(int wheelRadiusMm, int wheelDistanceMm,
            int motorFrontLeft, int motorFrontRight,
            int motorBackLeft, int motorBackRight) {
        checkNativeLoaded();
        initDifferentialDriveBase4WheelNative(nativePtr, wheelRadiusMm, wheelDistanceMm,
                motorFrontLeft, motorFrontRight,
                motorBackLeft, motorBackRight);
    }

    /**
     * Sets the values of the PID controller used for the motors
     * 
     * @param kp the Kp value
     * @param ki the Ki value
     * @param kd the Kd value
     */
    public void setPID(float kp, float ki, float kd) {
        checkNativeLoaded();
        setPIDNative(nativePtr, kp, ki, kd);
    }

    /**
     * Activates a limit switch
     * 
     * @param motor  the number of the motor (0, 2 or 3, motor 1 does not have
     *               interrupts)
     * @param high   switch low (0) or switch high (1)
     * @param enable enable (1) or disable (0) the switch
     * @param mode   normally open (0) or normally closed (1)
     * @param bounce bounceback activated(1) or deactivated(0)
     */
    public void setLimitSwitches(int motor, int high, int enable, int mode, int bounce) {
        checkNativeLoaded();
        setLimitSwitchesNative(nativePtr, motor, high, enable, mode, bounce);
    }

    /**
     * Set the RPM of a single motor
     * 
     * @param motor the number of the motor (0-3)
     * @param rpm   the desired RPM (-100 <= rpm <= 100)
     */
    public void setRPM(int motor, int rpm) {
        checkNativeLoaded();
        setRPMNative(nativePtr, motor, rpm);
    }

    /**
     * Sets the speed in percent of a single motor
     * 
     * @param motor   the number of the motor (0-3)
     * @param percent the speed in percent +/- 100
     */
    public void setSpeed(int motor, int percent) {
        checkNativeLoaded();
        setSpeedNative(nativePtr, motor, percent);
    }

    /**
     * Reset the encoder value
     * 
     * @param motor the number of the motor (0-3)
     */
    public void resetEncoder(int motor) {
        checkNativeLoaded();
        resetEncoderNative(nativePtr, motor);
    }

    /**
     * Stops all motors of the drive base
     */
    public void stop() {
        checkNativeLoaded();
        stopNative(nativePtr);
    }

    /**
     * Returns the value from an encoder
     * 
     * @param motor the number of the motor (0-3)
     * @return the encoder value
     */
    public int getEncoder(int motor) {
        checkNativeLoaded();
        return getEncoderNative(nativePtr, motor);
    }

    /**
     * Returns the RPM of a motor
     * 
     * @param motor the number of the motor (0-3)
     * @return the rpm
     */
    public int getRPM(int motor) {
        checkNativeLoaded();
        return getRPMNative(nativePtr, motor);
    }

    /**
     * Registers a function as callback function that gets triggered when a limit
     * switch triggers an interrupt
     * 
     * @param callback the callback function
     */
    public void registerLimitSwitchCallback(LimitSwitchCallback callback) {
        checkNativeLoaded();
        limitSwitchCallback = callback;
        registerLimitSwitchCallbackNative(nativePtr);
    }

    /**
     * Registers a function as a callback function that gets triggered when a drive
     * action completes
     * 
     * @param callback the callback function
     */
    public void registerDriveActionCallback(DriveActionCallback callback) {
        checkNativeLoaded();
        driveActionCallback = callback;
        registerDriveActionCallbackNative(nativePtr);
    }

    // Static methods called from native code - these must be static for JNI
    // callbacks
    private static void onLimitSwitchTriggered(int motorNr, int high) {
        if (limitSwitchCallback != null) {
            limitSwitchCallback.onLimitSwitchTriggered(motorNr, high);
        }
    }

    private static void onDriveActionComplete() {
        if (driveActionCallback != null) {
            driveActionCallback.onDriveActionComplete();
        }
    }

    /**
     * Sets a speed for the base in x-, y- and w-direction
     * 
     * @param xSpeedCms  speed in x direction in cm/s
     * @param ySpeedCms  speed in y direction in cm/s
     * @param wSpeedDegs speed in w direction in degrees/s
     */
    public void speedDrive(int xSpeedCms, int ySpeedCms, int wSpeedDegs) {
        checkNativeLoaded();
        speedDriveNative(nativePtr, xSpeedCms, ySpeedCms, wSpeedDegs);
    }

    /**
     * Sets a speed for the base in x-, y- and w-direction for a specified time
     * 
     * @param xSpeedCms  speed in x direction in cm/s
     * @param ySpeedCms  speed in y direction in cm/s
     * @param wSpeedDegs speed in w direction in degrees/s
     * @param timeS      time in seconds
     */
    public void timeDrive(int xSpeedCms, int ySpeedCms, int wSpeedDegs, int timeS) {
        checkNativeLoaded();
        timeDriveNative(nativePtr, xSpeedCms, ySpeedCms, wSpeedDegs, timeS);
    }

    /**
     * Drives the base for a specified distance
     * 
     * @param xMeter      distance to drive in x direction in m
     * @param yMeter      distance to drive in y direction in m
     * @param omegaDegree distance to drive in w direction in degrees
     */
    public void driveDistance(int xMeter, int yMeter, int omegaDegree) {
        checkNativeLoaded();
        driveDistanceNative(nativePtr, xMeter, yMeter, omegaDegree);
    }

    // Native method declarations
    private native long createNative();

    private native void destroyNative(long ptr);

    private native void initOmniDriveBaseNative(long ptr, int wheelRadiusMm, int robotRadiusMm,
            int motorLeft, int motorRight, int motorBack);

    private native void initMecanumDriveBaseNative(long ptr, int wheelRadiusMm, int robotXMm, int robotYMm,
            int motorFrontLeft, int motorFrontRight,
            int motorBackLeft, int motorBackRight);

    private native void initDifferentialDriveBase2WheelNative(long ptr, int wheelRadiusMm, int wheelDistanceMm,
            int motorLeft, int motorRight);

    private native void initDifferentialDriveBase4WheelNative(long ptr, int wheelRadiusMm, int wheelDistanceMm,
            int motorFrontLeft, int motorFrontRight,
            int motorBackLeft, int motorBackRight);

    private native void setPIDNative(long ptr, float kp, float ki, float kd);

    private native void setLimitSwitchesNative(long ptr, int motor, int high, int enable, int mode, int bounce);

    private native void setRPMNative(long ptr, int motor, int rpm);

    private native void setSpeedNative(long ptr, int motor, int percent);

    private native void resetEncoderNative(long ptr, int motor);

    private native void stopNative(long ptr);

    private native int getEncoderNative(long ptr, int motor);

    private native int getRPMNative(long ptr, int motor);

    private native void speedDriveNative(long ptr, int xSpeedCms, int ySpeedCms, int wSpeedDegs);

    private native void timeDriveNative(long ptr, int xSpeedCms, int ySpeedCms, int wSpeedDegs, int timeS);

    private native void driveDistanceNative(long ptr, int xMeter, int yMeter, int omegaDegree);

    private native void registerLimitSwitchCallbackNative(long ptr);

    private native void registerDriveActionCallbackNative(long ptr);
}

// JNI glue code template for at.jan.trummer.amcu.lib.AMCU
// Place this file in your native build, and link with amcu.cpp/amcu.h
// Adjust the C++ logic as needed for your project

#include <jni.h>
#include "amcu.h" // Your real C++ header

// Use a single static/global AMCU instance (not per-object)
static AMCU *g_amcu = nullptr;
static jobject g_limitSwitchCallback = nullptr;
static jobject g_driveActionCallback = nullptr;
static JavaVM *g_jvm = nullptr;

// Helper to attach current thread and get JNIEnv*
JNIEnv *getJNIEnv()
{
    JNIEnv *env = nullptr;
    if (g_jvm)
    {
        jint res = g_jvm->GetEnv((void **)&env, JNI_VERSION_1_6);
        if (res == JNI_EDETACHED)
        {
            g_jvm->AttachCurrentThread((void **)&env, nullptr);
        }
    }
    return env;
}

// Called when JNI is loaded, to store JavaVM pointer
jint JNI_OnLoad(JavaVM *vm, void *)
{
    g_jvm = vm;
    return JNI_VERSION_1_6;
}

extern "C"
{

    JNIEXPORT void JNICALL Java_com_frc_amcu_AMCUWrapper_initOmniDriveBaseNative(JNIEnv *env, jobject obj, jint wheelRadius, jint robotRadius, jint motorLeft, jint motorRight, jint motorBack)
    {
        if (!g_amcu)
            g_amcu = new AMCU();
        g_amcu->initOmniDriveBase(static_cast<uint8_t>(wheelRadius), static_cast<uint16_t>(robotRadius),
                                  static_cast<Motor>(motorLeft), static_cast<Motor>(motorRight), static_cast<Motor>(motorBack));
    }

    JNIEXPORT void JNICALL Java_com_frc_amcu_AMCUWrapper_initMecanumDriveBaseNative(JNIEnv *env, jobject obj, jint wheelRadius, jint robotX, jint robotY, jint motorFrontLeft, jint motorFrontRight, jint motorBackLeft, jint motorBackRight)
    {
        if (!g_amcu)
            g_amcu = new AMCU();
        g_amcu->initMecanumDriveBase(static_cast<uint8_t>(wheelRadius), static_cast<uint16_t>(robotX), static_cast<uint16_t>(robotY),
                                     static_cast<Motor>(motorFrontLeft), static_cast<Motor>(motorFrontRight), static_cast<Motor>(motorBackLeft), static_cast<Motor>(motorBackRight));
    }

    JNIEXPORT void JNICALL Java_com_frc_amcu_AMCUWrapper_initDifferentialDriveBase2WheelNative(JNIEnv *env, jobject obj, jint wheelRadius, jint wheelDistance, jint motorLeft, jint motorRight)
    {
        if (!g_amcu)
            g_amcu = new AMCU();
        g_amcu->initDifferentialDriveBase2Wheel(static_cast<uint8_t>(wheelRadius), static_cast<uint16_t>(wheelDistance),
                                                static_cast<Motor>(motorLeft), static_cast<Motor>(motorRight));
    }

    JNIEXPORT void JNICALL Java_com_frc_amcu_AMCUWrapper_initDifferentialDriveBase4WheelNative(JNIEnv *env, jobject obj, jint wheelRadius, jint wheelDistance, jint motorFrontLeft, jint motorFrontRight, jint motorBackLeft, jint motorBackRight)
    {
        if (!g_amcu)
            g_amcu = new AMCU();
        g_amcu->initDifferentialDriveBase4Wheel(static_cast<uint8_t>(wheelRadius), static_cast<uint16_t>(wheelDistance),
                                                static_cast<Motor>(motorFrontLeft), static_cast<Motor>(motorFrontRight), static_cast<Motor>(motorBackLeft), static_cast<Motor>(motorBackRight));
    }

    JNIEXPORT void JNICALL Java_com_frc_amcu_AMCUWrapper_setPIDNative(JNIEnv *env, jobject obj, jfloat kp, jfloat ki, jfloat kd)
    {
        if (g_amcu)
            g_amcu->setPID(kp, ki, kd);
    }

    JNIEXPORT void JNICALL Java_com_frc_amcu_AMCUWrapper_setLimitSwitchesNative(JNIEnv *env, jobject obj, jint motor, jint high, jint enable, jint mode, jint bounce)
    {
        if (g_amcu)
            g_amcu->setLimitSwitches(static_cast<Motor>(motor), static_cast<uint8_t>(high), static_cast<uint8_t>(enable), static_cast<uint8_t>(mode), static_cast<uint8_t>(bounce));
    }

    JNIEXPORT void JNICALL Java_com_frc_amcu_AMCUWrapper_setRPMNative(JNIEnv *env, jobject obj, jint motor, jint rpm)
    {
        if (g_amcu)
            g_amcu->setRPM(static_cast<Motor>(motor), static_cast<int8_t>(rpm));
    }

    JNIEXPORT void JNICALL Java_com_frc_amcu_AMCUWrapper_setSpeedNative(JNIEnv *env, jobject obj, jint motor, jint percent)
    {
        if (g_amcu)
            g_amcu->setSpeed(static_cast<Motor>(motor), static_cast<int8_t>(percent));
    }

    JNIEXPORT void JNICALL Java_com_frc_amcu_AMCUWrapper_resetEncoderNative(JNIEnv *env, jobject obj, jint motor)
    {
        if (g_amcu)
            g_amcu->resetEncoder(static_cast<Motor>(motor));
    }

    JNIEXPORT void JNICALL Java_com_frc_amcu_AMCUWrapper_stopNative(JNIEnv *env, jobject obj)
    {
        if (g_amcu)
            g_amcu->stop();
    }

    JNIEXPORT jshort JNICALL Java_com_frc_amcu_AMCUWrapper_getEncoderNative(JNIEnv *env, jobject obj, jint motor)
    {
        if (g_amcu)
            return g_amcu->getEncoder(static_cast<Motor>(motor));
        return 0;
    }

    JNIEXPORT jint JNICALL Java_com_frc_amcu_AMCUWrapper_getRPMNative(JNIEnv *env, jobject obj, jint motor)
    {
        if (g_amcu)
            return g_amcu->getRPM(static_cast<Motor>(motor));
        return 0;
    }

    JNIEXPORT void JNICALL Java_com_frc_amcu_AMCUWrapper_registerLimitSwitchCallbackNative(JNIEnv *env, jobject obj, jobject callback)
    {
        if (g_limitSwitchCallback)
        {
            env->DeleteGlobalRef(g_limitSwitchCallback);
            g_limitSwitchCallback = nullptr;
        }
        if (callback)
        {
            g_limitSwitchCallback = env->NewGlobalRef(callback);
        }
    }

    // Example: call this from C++ when the limit switch triggers
    void notifyLimitSwitchTriggered()
    {
        if (!g_limitSwitchCallback || !g_jvm)
            return;
        JNIEnv *env = getJNIEnv();
        jclass cbClass = env->GetObjectClass(g_limitSwitchCallback);
        jmethodID mid = env->GetMethodID(cbClass, "onLimitSwitchTriggered", "()V");
        if (mid)
        {
            env->CallVoidMethod(g_limitSwitchCallback, mid);
        }
    }

    JNIEXPORT void JNICALL Java_com_frc_amcu_AMCUWrapper_registerDriveActionCallbackNative(JNIEnv *env, jobject obj, jobject callback)
    {
        if (g_driveActionCallback)
        {
            env->DeleteGlobalRef(g_driveActionCallback);
            g_driveActionCallback = nullptr;
        }
        if (callback)
        {
            g_driveActionCallback = env->NewGlobalRef(callback);
        }
    }

    // Example: call this from C++ when a drive action occurs
    void notifyDriveAction()
    {
        if (!g_driveActionCallback || !g_jvm)
            return;
        JNIEnv *env = getJNIEnv();
        jclass cbClass = env->GetObjectClass(g_driveActionCallback);
        jmethodID mid = env->GetMethodID(cbClass, "onDriveAction", "()V");
        if (mid)
        {
            env->CallVoidMethod(g_driveActionCallback, mid);
        }
    }

    JNIEXPORT void JNICALL Java_com_frc_amcu_AMCUWrapper_speedDriveNative(JNIEnv *env, jobject obj, jint xSpeed, jint ySpeed, jint wSpeed)
    {
        if (g_amcu)
            g_amcu->speedDrive(static_cast<uint8_t>(xSpeed), static_cast<uint8_t>(ySpeed), static_cast<uint8_t>(wSpeed));
    }

    JNIEXPORT void JNICALL Java_com_frc_amcu_AMCUWrapper_timeDriveNative(JNIEnv *env, jobject obj, jint xSpeed, jint ySpeed, jint wSpeed, jint timeS)
    {
        if (g_amcu)
            g_amcu->timeDrive(static_cast<uint8_t>(xSpeed), static_cast<uint8_t>(ySpeed), static_cast<uint8_t>(wSpeed), static_cast<uint8_t>(timeS));
    }

    JNIEXPORT void JNICALL Java_com_frc_amcu_AMCUWrapper_driveDistanceNative(JNIEnv *env, jobject obj, jint xMeter, jint yMeter, jint omegaDegree)
    {
        if (g_amcu)
            g_amcu->driveDistance(static_cast<uint8_t>(xMeter), static_cast<uint8_t>(yMeter), static_cast<uint16_t>(omegaDegree));
    }

} // extern "C"

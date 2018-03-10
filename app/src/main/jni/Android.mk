LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := boardwalk2_jni

LOCAL_LDLIBS := -llog -lEGL
LOCAL_SRC_FILES := VMLauncher.c dlopen_jni.c potatobridge.c

include $(BUILD_SHARED_LIBRARY)

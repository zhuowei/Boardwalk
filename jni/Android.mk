LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)  
LOCAL_LDLIBS := -llog
LOCAL_MODULE    := boardwalk
LOCAL_SRC_FILES := main.c catcher.c

include $(BUILD_SHARED_LIBRARY)

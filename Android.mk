LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_STATIC_JAVA_LIBRARIES := tachograph
LOCAL_STATIC_JAVA_LIBRARIES += jheader		

LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_PACKAGE_NAME := CarLauncher

LOCAL_CERTIFICATE := platform
LOCAL_PRIVILEGED_MODULE := true
include $(BUILD_PACKAGE)


include $(CLEAR_VARS) 

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := tachograph:libs/tchip-tachograph.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += jheader:libs/jheader-0.1.jar	
		
include $(BUILD_MULTI_PREBUILT) 

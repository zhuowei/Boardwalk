#include <dlfcn.h>
#include <jni.h>
#include "gdvm.h"

#include <sys/mman.h>
#include <android/log.h>

#include <stdlib.h>

static void* libDvmHandle;

static void* getLibDvm() {
	if (!libDvmHandle) {
		libDvmHandle = dlopen("libdvm.so", RTLD_LAZY);
	}
	return libDvmHandle;
}

JNIEXPORT void JNICALL Java_net_zhuoweizhang_boardwalk_DalvikTweaks_nativeSetDefaultStackSize
  (JNIEnv *env, jclass clazz, jint size, jint androidBuild) {
	void* gDvm = dlsym(getLibDvm(), "gDvm");
	if (gDvm == NULL) return;
	if (androidBuild <= 16) { // 4.1 and below
		((struct DvmGlobals_ics*) gDvm)->stackSize = size;
	} else if (androidBuild <= 18) { // 4.2, 4.3
		((struct DvmGlobals_jbmr1*) gDvm)->stackSize = size;
	} else { // 4.4 and above
		((struct DvmGlobals*) gDvm)->stackSize = size;
	}
}

JNIEXPORT void JNICALL Java_net_zhuoweizhang_boardwalk_DalvikTweaks_crashTheLogger
  (JNIEnv *env, jclass clazz) {
	void* print = dlsym(RTLD_DEFAULT, "__android_log_print");
	int* printInt = (int*) print;
	__android_log_print(ANDROID_LOG_INFO, "Boardwalk", "Logger address: %x first bytes: %x", (int) print, printInt[0]);
	mprotect((void*) (((uintptr_t) print / 4096) * 4096), 4096, PROT_NONE);
}

JNIEXPORT void JNICALL Java_net_zhuoweizhang_boardwalk_DalvikTweaks_setenv
  (JNIEnv *env, jclass clazz, jstring nameStr, jstring valueStr, jboolean overwrite) {
	const char* name = (*env)->GetStringUTFChars(env, nameStr, NULL);
	const char* value = (*env)->GetStringUTFChars(env, valueStr, NULL);
	setenv(name, value, overwrite);
	(*env)->ReleaseStringUTFChars(env, nameStr, name);
	(*env)->ReleaseStringUTFChars(env, valueStr, value);
}

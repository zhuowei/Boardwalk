#include <jni.h>
#include <unistd.h>
#include <dlfcn.h>
#include <android/log.h>

JNIEXPORT jboolean JNICALL Java_net_zhuoweizhang_boardwalk_potato_LoadMe_nativeDlopen
  (JNIEnv *env, jclass clazz, jstring name) {
	const char* nameUtf = (*env)->GetStringUTFChars(env, name, NULL);
	void* handle = dlopen(nameUtf, RTLD_GLOBAL | RTLD_LAZY);
	if (!handle) {
		__android_log_print(ANDROID_LOG_ERROR, "Boardwalk2", "Failed to dlopen %s: %s", nameUtf, dlerror());
	}
	(*env)->ReleaseStringUTFChars(env, name, nameUtf);
	return handle != NULL;
}

JNIEXPORT jint JNICALL Java_net_zhuoweizhang_boardwalk_potato_LoadMe_chdir
  (JNIEnv *env, jclass clazz, jstring nameStr) {
	const char* name = (*env)->GetStringUTFChars(env, nameStr, NULL);
	int retval = chdir(name);
	(*env)->ReleaseStringUTFChars(env, nameStr, name);
	return retval;
}

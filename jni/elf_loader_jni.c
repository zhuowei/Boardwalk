#include <stdlib.h>
#include <fcntl.h>
#include <jni.h>
#include <dlfcn.h>
#include <stdio.h>
#include <pthread.h>
#include "net_zhuoweizhang_boardwalk_potato_LoadMe.h"
#include "potatobridge.h"
#include <EGL/egl.h>
#include <stdbool.h>

int PotatoExec(void* src_auxv, size_t src_auxv_size,
	int argc, char **argv);

struct PotatoBridge potatoBridge;

JNIEXPORT void JNICALL Java_net_zhuoweizhang_boardwalk_potato_LoadMe_potatoExec
  (JNIEnv *env, jclass clazz, jbyteArray auxvArray, jobjectArray argsArray) {
	size_t src_auxv_size = (*env)->GetArrayLength(env, auxvArray);
	jbyte src_auxv[src_auxv_size];
	(*env)->GetByteArrayRegion(env, auxvArray, 0, src_auxv_size, src_auxv);
	int argc = (*env)->GetArrayLength(env, argsArray);
	char* argv[argc];
	for (int i = 0; i < argc; i++) {
		jstring str = (*env)->GetObjectArrayElement(env, argsArray, i);
		int len = (*env)->GetStringUTFLength(env, str);
		char* buf = malloc(len + 1);
		int characterLen = (*env)->GetStringLength(env, str);
		(*env)->GetStringUTFRegion(env, str, 0, characterLen, buf);
		buf[len] = 0;
		argv[i] = buf;
	}
	PotatoExec(src_auxv, src_auxv_size, argc, argv);
}

JNIEXPORT void JNICALL Java_net_zhuoweizhang_boardwalk_potato_LoadMe_setenv
  (JNIEnv *env, jclass clazz, jstring nameStr, jstring valueStr) {
	const char* name = (*env)->GetStringUTFChars(env, nameStr, NULL);
	const char* value = (*env)->GetStringUTFChars(env, valueStr, NULL);
	setenv(name, value, true);
	(*env)->ReleaseStringUTFChars(env, nameStr, name);
	(*env)->ReleaseStringUTFChars(env, valueStr, value);
}

JNIEXPORT void JNICALL Java_net_zhuoweizhang_boardwalk_potato_LoadMe_redirectStdio
  (JNIEnv *env, jclass clazz) {
	int outputfd = open("/sdcard/potato_output.txt", O_WRONLY | O_CREAT | O_TRUNC, 0666);
	dup2(outputfd, 1);
	dup2(outputfd, 2);
}

JNIEXPORT void JNICALL Java_net_zhuoweizhang_boardwalk_potato_LoadMe_setupBridgeEGL
  (JNIEnv* env, jclass clazz) {
	potatoBridge.eglContext = eglGetCurrentContext();
	potatoBridge.eglDisplay = eglGetCurrentDisplay();
	potatoBridge.eglReadSurface = eglGetCurrentSurface(EGL_READ);
	potatoBridge.eglDrawSurface = eglGetCurrentSurface(EGL_DRAW);
}

JNIEXPORT void JNICALL Java_net_zhuoweizhang_boardwalk_potato_LoadMe_setupBridge
  (JNIEnv *env, jclass clazz) {
	potatoBridge.dlsym = &dlsym;
	potatoBridge.dlopen = &dlopen;
	potatoBridge.dlclose = &dlclose;
	potatoBridge.dlerror = &dlerror;
	potatoBridge.dladdr = &dladdr;
	potatoBridge.pthread_create = &pthread_create;
	char envbuf[0x100];
	snprintf(envbuf, sizeof(envbuf), "%p", &potatoBridge);
	setenv("POTATO_BRIDGE", envbuf, 1);
}

JNIEXPORT jint JNICALL Java_net_zhuoweizhang_boardwalk_potato_LoadMe_chdir
  (JNIEnv *env, jclass clazz, jstring nameStr) {
	const char* name = (*env)->GetStringUTFChars(env, nameStr, NULL);
	int retval = chdir(name);
	(*env)->ReleaseStringUTFChars(env, nameStr, name);
	return retval;
}

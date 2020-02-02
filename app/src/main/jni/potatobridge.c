#include <jni.h>
#include <dlfcn.h>
#include <EGL/egl.h>
#include <android/log.h>
#include <stdlib.h>

#include "potatobridge.h"
struct PotatoBridge potatoBridge;

JNIEXPORT void JNICALL Java_net_zhuoweizhang_boardwalk_potato_LoadMe_setupBridgeEGL
  (JNIEnv* env, jclass clazz) {
	potatoBridge.eglContext = eglGetCurrentContext();
	potatoBridge.eglDisplay = eglGetCurrentDisplay();
	potatoBridge.eglReadSurface = eglGetCurrentSurface(EGL_READ);
	potatoBridge.eglDrawSurface = eglGetCurrentSurface(EGL_DRAW);
}

JNIEXPORT void JNICALL Java_net_zhuoweizhang_boardwalk_potato_LoadMe_initializeGl4Es
		(JNIEnv* env, jclass clazz, jstring path) {
	const char* pathUtf = (*env)->GetStringUTFChars(env, path, NULL);
	dlopen(pathUtf, RTLD_LAZY | RTLD_LOCAL);
	(*env)->ReleaseStringUTFChars(env, path, pathUtf);
}

// Called from JNI_OnLoad of liblwjgl_opengl32
void boardwalk2_openGLOnLoad() {
	EGLBoolean success = eglMakeCurrent(potatoBridge.eglDisplay, potatoBridge.eglDrawSurface, potatoBridge.eglReadSurface, potatoBridge.eglContext);
	__android_log_print(ANDROID_LOG_INFO, "Boardwalk", "openGLOnLoad: eglMakeCurrent was %s", success? "ok": "bad");
}

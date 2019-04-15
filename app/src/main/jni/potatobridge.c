#include <jni.h>
#include <dlfcn.h>
#include <EGL/egl.h>

#include "potatobridge.h"
struct PotatoBridge potatoBridge;

JNIEXPORT void JNICALL Java_net_zhuoweizhang_boardwalk_potato_LoadMe_setupBridgeEGL
  (JNIEnv* env, jclass clazz) {
	potatoBridge.eglContext = eglGetCurrentContext();
	potatoBridge.eglDisplay = eglGetCurrentDisplay();
	potatoBridge.eglReadSurface = eglGetCurrentSurface(EGL_READ);
	potatoBridge.eglDrawSurface = eglGetCurrentSurface(EGL_DRAW);
}

// Called from JNI_OnLoad of liblwjgl_opengl32
void boardwalk2_openGLOnLoad() {
	eglMakeCurrent(potatoBridge.eglDisplay, potatoBridge.eglDrawSurface, potatoBridge.eglReadSurface, potatoBridge.eglContext);
}

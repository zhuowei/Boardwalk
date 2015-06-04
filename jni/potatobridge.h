struct PotatoBridge {
	void* (*dlsym)(void* handle, const char* name);
	void* (*dlopen)(const char* name, int flag);
	int (*dlclose)(void* handle);
	const char* (*dlerror)();
	int (*dladdr)(const void *addr, void *info);
	int (*pthread_create)(pthread_t *thread, const pthread_attr_t *__attr,
                             void *(*start_routine)(void*), void *arg);
	void* eglContext;
	void* eglDisplay;
	void* eglReadSurface;
	void* eglDrawSurface;
};

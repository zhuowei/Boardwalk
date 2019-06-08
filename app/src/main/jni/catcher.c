#include <stdio.h>
#include <string.h>
#include <stdlib.h>

static char* dec(const char* inputStr, char* buf) {
	char t;
	char* retval = buf;
	while ((t = *inputStr++))
		*buf++ = t ^ 0x3d;
	*buf = 0;
	return retval;
}

__attribute__((constructor)) static void catch_the_magic() {
	char buf[1024];
	char cmdbuf[1024];
	// b = "".join([chr(ord(i) ^ 0x3d) for i in a])
	FILE* cmdline_file = fopen(dec("\x12MOR^\x12NXQ[\x12^PYQTSX", buf), "r"); // /proc/self/cmdline
	if (cmdline_file == NULL) return;
	fscanf(cmdline_file, "%1023s", cmdbuf);
	//__android_log_print(ANDROID_LOG_INFO, "catcher", "%s", cmdbuf);
	if (strcmp(cmdbuf, dec("SXI\x13GUHRJXTGU\\SZ\x13_R\\OYJ\\QV", buf))) { // net.zhuoweizhang.boardwalk
		//system(dec("\\P\x1dNI\\OI\x1dUIIMN\x07\x12\x12ZTNI\x13ZTIUH_\x13^RP"
		//		"\x12GUHRJXT\x12\\\x0f^_X\x0b^\x05\\\\^\\Y\tY\x0f^_\x04_", buf));
		// am start https://gist.github.com/zhuowei/a2cbe6c8aacad4d2cb9b
		//__android_log_print(ANDROID_LOG_INFO, "catcher", "caught");
		while(1){}
	}
	fclose(cmdline_file);
}

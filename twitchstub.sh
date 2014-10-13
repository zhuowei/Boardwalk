touch nonexistantstub.c
arm-linux-androideabi-gcc -c -Wall -Werror -fpic nonexistantstub.c
arm-linux-androideabi-gcc -shared -nostdlib -o libzhuowei_this_library_should_never_ever_exist_ever_oh_wow_look_at_the_time_gotta_go.so nonexistantstub.o
mv libzhuowei_this_library_should_never_ever_exist_ever_oh_wow_look_at_the_time_gotta_go.so jni/


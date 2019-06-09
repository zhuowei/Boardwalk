## Boardwalk: a Minecraft Java Edition launcher for Android 
This is the source code for Boardwalk, a Minecraft Java Edition launcher for Android. 
## Branches 
This branch contains the source for Boardwalk 1.2 (the last public released version). 
## Additional components 
- [LWJGL Android port](https://github.com/BoardwalkApp/boardwalk-lwjgl)
- [ptitSeb's gl4es](https://github.com/ptitSeb/gl4es)
- [iPaulPro's aFileChooser](https://github.com/iPaulPro/aFileChooser)
- [apportable's OpenAL_soft](https://github.com/apportable/openal-soft)
## Features
- Use Gradle to build,so it can be imported by AndroidStudio.
- Reconstructing the project structure, most of them can be compiled directly and completely.
- Most of JNI compilations are linked through Gradle script.
## Additional components
Part of the components used by the project:
- [LWJGL Android port](https://github.com/BoardwalkApp/boardwalk-lwjgl)
- [ptitSeb's gl4es](https://github.com/ptitSeb/gl4es)
- [aFileChooser](https://github.com/zhuowei/aFileChooser)
- [OpenAL_soft](https://github.com/zhuowei/openal-soft)
## Building
>>### Download code
>>>>- ```Shell: git clone [The Git URL]```
>>### Configuration environment
>>>>- Download and Install AndroidStudio
>>>>- Android SDK Version: API 27,API 19
>>>>- SDK build-tools: 28.0.3
>>>>- Android SDK platforms
>>>>- Android NDK
>>### Compile
>>>>- Import this project.And if everything works, you should be able to compile.
## Known Issues
>>>>- Can't build lwjgl in AndroidStudio now because it uses Ant build system.
>>>>>>>In this branch,Lwjgl is added as JAR library and Lwjgl jni is added as JniLibs to avoid compiling problems.
>>>>- The theme of the application is incorrect.
>>>>- Document selection is unavailable due to lack of authorization.(Android 6.0 or above.)
>>>>- Collapse immediately after launching Minecraft.(Need to debug)
>>>>- Can't run on Android 7.0 or above.
## Future
This branch is in its infancy and has not yet fully implemented the correct compilation of Boardwalk. 
These functions will be improved and revised in the future. 
(p.s. as a high school student in china,it is hard to hava extra time to work on this ;) )


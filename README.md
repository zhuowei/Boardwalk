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
## Some_Mincraft_Packs
>>>>- 这里是一些经过修改的Minecraft游戏文件。
>>>>- 首先，我选择了几款无需Forge/Lite API支持的Mod，比如旧版的Optifine 或者 TMI内置修改器。
>>>>- 其次，我将它们覆盖到相应版本.jar原文件中,替换了Boardwalk的Version目录。
>>>>- 然后，Boardwalk对它进行了rename和dx的一些操作得到.dex文件
>>>>- 然后，我通过Boardwalk启动它们，会遇到一些错误导致程序崩溃。
>>>>- 这时，我通过解读崩溃日志，寻找错误原因，并尝试通过反编译的逆向手段修改smali代码
>>>>>> 修改的代码涉及 Boardwalk-makeshift的awt部分，还有Optifine与TMI的几处代码
>>>>- 最终，可以成功运行Optifine和TMI，并且将这一效果应用到了Minecraft 1.7.x/1.8.x
>>>>- 问题，我只是在自己的设备上实现了这些，还不能给出一般的操作步骤。并且，对Makeshift的逆向修改还开始没有转为正向代码。
>>>>>> 由于安卓DVM虚拟机和JVM虚拟机的差异明显，Forge API的加载在ClassLoader调用时就已经崩溃。同样ASM可能也不能实现。
>>>>- 我有几张相关的照片已经上传。
## Future
This branch is in its infancy and has not yet fully implemented the correct compilation of Boardwalk. 
These functions will be improved and revised in the future. 

(p.s. as a high school student in china,it is hard to hava extra time to work on this ;) )


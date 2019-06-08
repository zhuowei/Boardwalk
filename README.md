# initial README
## Boardwalk: a Minecraft Java Edition launcher for Android 
This is the source code for Boardwalk, a Minecraft Java Edition launcher for Android. 
## Branches 
This branch contains the source for Boardwalk 1.2 (the last public released version). 
There are three branches of development: 
- `master`: Boardwalk 1.0-1.2. Uses Android's own JVM. The only released version. Doesn't work on Android 7.x and above.
- `boardwalk-1.9`: Boardwalk 1.9 beta. Uses Oracle's JDK. Abandoned.
- `boardwalk-2.0`: Boardwalk 2.0. Uses OpenJDK's Android port. This is the latest version. 
## Additional components 
Some modified third-party components are in separate repositories: 
- [LWJGL Android port](https://github.com/BoardwalkApp/boardwalk-lwjgl)
- [lunixbochs' and ptitSeb's glShim](https://github.com/BoardwalkApp/boardwalk-glshim) 
## Building 
TODO - the current Boardwalk build process only works on my computer; I will post updated instructions soon. 
## License 
The source code in this repository is licensed under the Apache License, version 2.0 unless otherwise indicated in the file. 
This means you can use those files as long as you credit me. 
Some files and libraries are taken from third-party projects and have their own licenses. Please see the header of those files for their licenses. 
If you have any questions, please open an issue. 
## Code of Conduct 
This project is governed by the Contributor Covenant version 1.4(https://www.contributor-covenant.org/version/1/4/code-of-conduct.html). 
# New READNE About This Branch  
## Features
- Use Gradle to build,so it can be imported by AndroidStudio eg.
- Reconstructing the project structure, most of them can be compiled directly and completely.
- Most of JNI compilations are linked through Gradle script.
## Additional components
Base [BoardwalkAPP](https://github.com/BoardwalkAPP)
Part of the components used by the project:
- [LWJGL Android port](https://github.com/BoardwalkApp/boardwalk-lwjgl)
- [ptitSeb's gl4es](https://github.com/ptitSeb/gl4es)
- [iPaulPro's aFileChooser](https://github.com/iPaulPro/aFileChooser)
- [apportable's OpenAL_soft](https://github.com/apportable/openal-soft)
## Building
>>### Download code
>>>>- ```Shell git clone https://github.com/longjunyu2/Boardwalk.git```
>>### Configuration environment
>>>>- Install AndroidStudio or other IDE with Gradle tools such as IDEA.
>>>>- Android SDK version:27,19
>>>>- SDK build-tools: 28.0.3
>>>>- Android 8.1,4.4 SDK platforms
>>### Compile
>>>>- Import this project.And if everything works, you should be able to compile.
## Known Issues
>>>>- The theme of the application is incorrect.
>>>>- Document selection is unavailable due to lack of authorization.
>>>>- Collapse immediately after launching Minecraft.(Need to debug)
>>>>- Can't run on Android 7.0 or above.
## Future
This branch is in its infancy and has not yet fully implemented the correct compilation of Boardwalk. 
These functions will be improved and revised in the future.

(p.s. as a high school student in china,it is hard to hava extra time to work on this ;) )

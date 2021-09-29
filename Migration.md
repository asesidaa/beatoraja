# Migration note

## Build system

The build system has been migrated to maven so that we can change the version of libraries and add new libraries easily.
Also, using maven plugins, we can automatically package a jar with almost all the dependencies (including javafx),
except for portaudio (currently).

Now, we can use

```shell
$ mvn -Djavacpp.platform=windows-x86_64 package
```

to build and generate the target jar file.

Notice that the platform can be changed to match the specified platform. See
the [wiki](https://github.com/bytedeco/javacpp-presets/wiki/Reducing-the-Number-of-Dependencies) of javacpp for more
detail.

## Backend (Experimental)

The backend has also changed from lwjgl2 to lwjgl3, since lwjgl2 has become legacy.

As the backend changes, the controller backend also changed. Now the controller backend for desktop is jamepad.

However, with the new controller backend, for the controller to be recognised, the controller should be added to
gamecontrollerdb.txt. I have already added PHOENIXWAN(HID mode) to the file.

To add a new controller, refer to this [tool](https://www.generalarcade.com/gamepadtool/).
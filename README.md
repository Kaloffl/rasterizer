# rasterizer
a triangle rasterizer that is fully implemented in software

## building & testing

You need to have a jdk 1.8 and scala sdk 2.12 in your PATH variable.
Make sure your working directory is the base of this project.
Execute the build_all.bat script to compile all files in the project.
Execute run.bat with the name of the class you want to run as the argument.
For example: > run.bat ExampleBox

A window should open with an example scene displayed in it.

## usage

You can navigate the 3d scene with the following controls:
Mouse drag: rotate camera
w, a, s, d: move forward, left, backward, right
shift, ctrl: move up, move down
ctrl + c: copy the current frame to the clipboard

## credits

Dragon model and texture copied from: https://github.com/kosua20/herebedragons
#!/bin/bash

# build script for java server
find webmate/ -name *.java > comp.list
javac @comp.list
jar cf webmate.jar webmate/
rm comp.list

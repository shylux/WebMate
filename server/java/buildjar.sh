#!/bin/bash

# build script for java server
find webmate/ -name *.java > comp.list
javac @comp.list
jar cfe webmate.jar webmate/sample/SimpleWebMateService webmate
rm comp.list

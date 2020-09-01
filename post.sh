#!/bin/bash -e
7z d sireum-kekinian-intellij.zip sireum-kekinian-intellij/lib/sireum.jar
VER=$(git log -n 1 --date=format:%Y%m%d --pretty=format:4.%cd.%h)
7z x sireum-kekinian-intellij.zip sireum-kekinian-intellij/lib/sireum-kekinian-intellij.jar
7z x sireum-kekinian-intellij/lib/sireum-kekinian-intellij.jar META-INF/plugin.xml
sed -i.bak "s/5.0.0-SNAPSHOT/${VER}/g" META-INF/plugin.xml
7z a sireum-kekinian-intellij/lib/sireum-kekinian-intellij.jar META-INF/plugin.xml
7z a sireum-kekinian-intellij.zip sireum-kekinian-intellij/lib/sireum-kekinian-intellij.jar
rm -fR META-INF sireum-kekinian-intellij

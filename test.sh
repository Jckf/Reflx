#!/bin/sh

[ -d TestServer ] || mkdir TestServer

cd TestServer

[ -f minecraft_server.jar ] || wget https://s3.amazonaws.com/Minecraft.Download/versions/1.7.10/minecraft_server.1.7.10.jar -O minecraft_server.jar

[ -d plugins ] && rm -rf plugins/* || mkdir plugins

cp ../SamplePlugin/target/SamplePlugin-*.jar plugins

java -Xmx512M -jar ../Reflx/target/Reflx-*.jar

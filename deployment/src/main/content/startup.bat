@echo off
SetLocal EnableDelayedExpansion
set JAVA=C:\jdk1.8.0_91\jre\bin\java.exe
rem set DUMP=-Dcom.sun.xml.ws.transport.http.HttpAdapter.dump=true
rem -Dlogback.configurationFile=/path/to/config.xml

%JAVA% -d64 -server -XX:+UseG1GC -XX:+AggressiveOpts -XX:+UseLargePages -Xmn1g  -Xms6g -Xmx6g  -XX:MaxGCPauseMillis=2000 -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -Xloggc:gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=15 -XX:GCLogFileSize=200M -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=heap_dump.hprof %DUMP% -cp ./*;lib/* -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5105 mc.minicraft.server.App

export JAVA=/usr/lib/jvm/jdk1.8.0_31/bin/java
#export DUMP=-Dcom.sun.xml.ws.transport.http.HttpAdapter.dump=true
# -Dlogback.configurationFile=/path/to/config.xml

$JAVA -d64 -server -XX:+UseG1GC -XX:+AggressiveOpts -XX:+UseLargePages -Xmn1g  -Xms6g -Xmx6g  -XX:MaxGCPauseMillis=2000 $DUMP -cp ./*;lib/* -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5105 mc.minicraft.server.App

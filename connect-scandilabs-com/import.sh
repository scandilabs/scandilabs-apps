#!/bin/bash

# verify environment
if [ ! -d "$CAT_TOMCAT_ROOT" ]
then  echo "Environment not initialized, run 'source initenv' in this Terminal window 
first."
  exit 1
fi

LIB=$CAT_TOMCAT_CURRENT_WEBAPP/WEB-INF/lib
CLASSES=$CAT_TOMCAT_CURRENT_WEBAPP/WEB-INF/classes
JAVA_BIN=/usr/bin

CLASSPATH=$(echo "$LIB"/*.jar | tr ' ' ':')

echo CLASSES: $CLASSES
CLASSPATH=$CLASSPATH:$CLASSES
echo Classpath: $CLASSPATH

# include this line below to enable remote debugging
#    -Xrunjdwp:transport=dt_socket,address=${DEBUG_PORT},server=y,suspend=n \

# production memory heap
# -Xms1024m -Xmx1024m

#
# Start the service in a JVM.
${JAVA_BIN}/java \
    -Xdebug \
    -Djava.net.preferIPv4Stack=true \
    -classpath $CLASSPATH \
    org.catamarancode.connect.csv.GoogleContactsLoader \
    $1 


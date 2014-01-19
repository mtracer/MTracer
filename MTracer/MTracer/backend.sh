#!/bin/bash

UDPSOURCE=10.107.100.85:7831
BASE=`dirname ${0}`

if [ $# != 0 ]; then
  if [ $1 == "-h" ]; then
    echo -e "Usage: backend.sh [X-Trace/data/dir]\nNOTE: default directory is ../data"
    exit
  else
    if [ -d $1 ]; then
      DATA_DIR=$1
    else
      echo "WARNING: Invalid data directory provided, using default ($BASE/data) instead"
      DATA_DIR=$BASE/data
    fi
  fi
else
  echo "Using default data directory: $BASE/data"
  DATA_DIR=$BASE/data
fi
LIB=$BASE/lib

CLASSPATH=.:$BASE:$BASE/bin:$LIB:$LIB/commons-collections-3.1.jar:$LIB/oro-2.0.8.jar:$LIB/mysql-connector-java-5.1.24-bin.jar:$LIB/commons-lang-2.1.jar:$LIB/servlet-api-2.4.jar:$LIB/derbyclient.jar:$LIB/slf4j-api-1.6.4.jar:$LIB/derby.jar:$LIB/slf4j-log4j12-1.6.4.jar:$LIB/derbynet.jar:$LIB/slf4j-log4j12-1.6.4-sources.jar:$LIB/derbytools.jar:$LIB/thrift-1.0.jar:$LIB/jetty-6.0.2.jar:$LIB/velocity-1.5.jar:$LIB/jetty-util-6.0.2.jar:$LIB/log4j-1.2.14.jar:$LIB/MyXTrace.jar

java  -Dxtrace.udpsource=$UDPSOURCE -cp $CLASSPATH edu.berkeley.xtrace.server.MT_MySQLXTraceServer


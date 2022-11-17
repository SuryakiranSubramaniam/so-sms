#! /bin/bash

# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#title           :sfmc-smshandler.sh
#description     :This script manages sfmc-smshandler Process.
#author          :SIFT SO Team
#version         :0.0.1    
#usage           :bash sfmc-smshandler.sh
#handler_version :0.0.1-release
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

#source ./so-env.sh

: "${JAVA_HOME?JAVA_HOME not set}"
: "${SO_HOME?SO_HOME not set}"

# ***********************************************
SCRIPT_HOME=$(dirname "$0")

echo "sfmc-smshandler-0.0.1"
export API_CONF=$SO_HOME'/conf'
export LIB_HOME=$SO_HOME'/lib'
export LOG_HOME=$SO_HOME'/log'
export LOGBACK_XML=$API_CONF'/sfmc-smshandler-logback.xml'
export FLOW_LOC=$SO_HOME'/flow'
export CAMEL_ENCRYPTION_PASSWORD=secret
export SO_ENCRYPT_ALGORITHM=PBEWITHMD5ANDDES

CP=$(echo ../lib/*.jar | tr ' ' ':')
export CLASSPATH=$API_CONF':'$CP':'$FLOW_LOC

GRAFANA_ARGS='-javaagent:'$LIB_HOME'/jmx_prometheus_javaagent-0.12.0.jar=54321:'$API_CONF'/jmx_export.yaml'
GRAFANA_ARGS=$GRAFANA_ARGS' -Dorg.apache.camel.jmx=true -Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.port=5555'
ARGS='-Dlogback.configurationFile='$LOGBACK_XML' -DCONFIG_HOME='$API_CONF' -DLOG_HOME='$LOG_HOME' '$GRAFANA_ARGS' -jar '$FLOW_LOC'/sfmc-smshandler-0.0.1.jar'
DAEMON=$JAVA_HOME/bin/java

case "$1" in
start)
    (
		pid=`pgrep -f '.+sfmc-smshandler-.+.jar'`
		if [ ! -z $pid ]; then 
			echo "process found with pid "$pid
			echo "use $0 stop"
		else 
			echo 'Starting...'
    		$DAEMON $ARGS #> $LOG_HOME/sfmc-smshandler_sysout.log 2>&1
			echo $!
		fi
	) #& 
;;

status)
	pid=`pgrep -f '.+sfmc-smshandler-.+.jar'`
	if [ ! -z $pid ]; then 
		echo "process found with pid "$pid
	else 
		echo "process not found"
	fi
;;

stop)
	pid=`pgrep -f '.+sfmc-smshandler-.+.jar'`
	if [ ! -z $pid ]; then 
		echo "stopping ..."$pid
		pkill -f '.+sfmc-smshandler-.+.jar'
	else 
		echo "process not found"
	fi
;;

kill)
	pid=`pgrep -f '.+sfmc-smshandler-.+.jar'`
	if [ ! -z $pid ]; then 
		echo "killing ..."$pid
		pkill -9 -f '.+sfmc-smshandler-.+.jar'
	else 
		echo "process not found"
	fi
;;

log)
	tail -f $LOG_HOME/sfmc-smshandler.log
;;

restart)
    $0 stop
    $0 start
;;

*)
    echo "Usage: $0 {status|start|stop}"
    exit 1
esac
unset CAMEL_ENCRYPTION_PASSWORD

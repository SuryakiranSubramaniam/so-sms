#! /bin/bash

# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#title           :${project.artifactId}.sh
#description     :This script manages ${project.artifactId} Process.
#author          :SIFT SO Team
#version         :${project.version}    
#usage           :bash ${project.artifactId}.sh
#handler_version :${project.version}-release
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

source ./so-env.sh

: "${JAVA_HOME?JAVA_HOME not set}"
: "${SO_HOME?SO_HOME not set}"

# ***********************************************
SCRIPT_HOME=$(dirname "$0")

echo "${project.artifactId}-${project.version}"
export API_CONF=$SO_HOME'/conf'
export LIB_HOME=$SO_HOME'/lib'
export LOG_HOME=$SO_HOME'/log'
export LOGBACK_XML=$API_CONF'/${project.artifactId}-logback.xml'
export FLOW_LOC=$SO_HOME'/flow'
export CAMEL_ENCRYPTION_PASSWORD=secret
export SO_ENCRYPT_ALGORITHM=PBEWITHMD5ANDDES

CP=$(echo ../lib/*.jar | tr ' ' ':')
export CLASSPATH=$API_CONF':'$CP':'$FLOW_LOC

GRAFANA_ARGS='-javaagent:'$LIB_HOME'/jmx_prometheus_javaagent-0.12.0.jar=54321:'$API_CONF'/jmx_export.yaml'
GRAFANA_ARGS=$GRAFANA_ARGS' -Dorg.apache.camel.jmx=true -Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.port=5555'
ARGS='-Dlogback.configurationFile='$LOGBACK_XML' -DCONFIG_HOME='$API_CONF' -DLOG_HOME='$LOG_HOME' '$GRAFANA_ARGS' -jar '$FLOW_LOC'/${project.artifactId}-${project.version}.jar'
DAEMON=$JAVA_HOME/bin/java

case "$1" in
start)
    (
		pid=`pgrep -f '.+${project.artifactId}-.+.jar'`
		if [ ! -z $pid ]; then 
			echo "process found with pid "$pid
			echo "use $0 stop"
		else 
			echo 'Starting...'
    		$DAEMON $ARGS > $LOG_HOME/${project.artifactId}_sysout.log 2>&1
			echo $!
		fi
	) & 
;;

status)
	pid=`pgrep -f '.+${project.artifactId}-.+.jar'`
	if [ ! -z $pid ]; then 
		echo "process found with pid "$pid
	else 
		echo "process not found"
	fi
;;

stop)
	pid=`pgrep -f '.+${project.artifactId}-.+.jar'`
	if [ ! -z $pid ]; then 
		echo "stopping ..."$pid
		pkill -f '.+${project.artifactId}-.+.jar'
	else 
		echo "process not found"
	fi
;;

kill)
	pid=`pgrep -f '.+${project.artifactId}-.+.jar'`
	if [ ! -z $pid ]; then 
		echo "killing ..."$pid
		pkill -9 -f '.+${project.artifactId}-.+.jar'
	else 
		echo "process not found"
	fi
;;

log)
	tail -f $LOG_HOME/${project.artifactId}.log
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
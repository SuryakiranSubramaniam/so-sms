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
export API_CONF='/opt/knowesis/sift/orchestrator/conf'
export LIB_HOME='/opt/knowesis/sift/orchestrator/lib'
export LOG_HOME='/opt/knowesis/sift/orchestrator/log'
export LOGBACK_XML=$API_CONF'/sfmc-smshandler-logback.xml'
export FLOW_LOC='/opt/knowesis/sift/orchestrator/flow'
export CAMEL_ENCRYPTION_PASSWORD=secret
export SO_ENCRYPT_ALGORITHM=PBEWITHMD5ANDDES

export SO_HOME=$(dirname `pwd`)
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk
export SO_SMS_INPUT_TOPIC=so.sms.in
export SIFT_QUEUES=localhost:9091,localhost:9092
export SO_LOG_LEVEL=debug
export LOG_TARGET=file

export AUTO_OFFSET_RESET=latest
export MAX_POLL_RECORDS_CONFIG=20
export MAX_POLL_INTERVAL_MS_CONFIG=60000
export AUTH_REQUEST_URL=mc63kdf7d4l9r0c3-0njhd1ss851.auth.marketingcloudapis.com/v2/token
export SMS_REGISTER_CONTACT_REQUEST_URL=localhost:10060/runtime/api/v2/registercontact
export SMS_REQUEST_URL_PATH=/messaging/v1/sms/messages/
export AUTH_TOKEN_REQUEST=n4IHnkG9jCgZ0I+wsV8Jog8TJ4Cj0GoDIv1fUH9MCuziVPMIj+a8pEMhelaKo8dxTR5s/WsPPPQeXTgjuN9Gnk5ruZLIVa71SPfgKjzYTWDFs5Icjt5cQeK/oC0p7iAXff+QMlVPN1LfvMsF8XQJZkGRLw4yCXX9xon4U/UtCwW68iwX1bsBHDN23Y0NIrKUe0gldg9dBNQtsaHGSucHDQ==
export GROUP_ID=smsHandler
export AGGREGATION_COMPLETION_TIMEOUT=100
export AGGREGATION_COMPLETION_SIZE=1
export SEDA_SMSAPI_SIZE=100
export SEDA_SMSAPI_CONCURRENT_CONSUMERS=10
export SEDA_CONTACT_POLICY_SIZE=100
export SEDA_CONTACT_POLICY_CONSUMERS=10
export SEDA_REGISTER_CONTACT_SIZE=100
export SEDA_REGISTER_CONTACT_CONCURRENT_CONSUMERS=10
export SO_WHITE_LISTED_NUMBERS=66959236556,8281401702,6598143232,0477704537,0457412087
export COUNTRY_CODE=91
export SO_WHITELISTING_ENABLE=true
export TRIGGER_SOURCE_CORE=CORE
export TRIGGER_SOURCE_SFMC=MC
export ENABLE_REGISTER_CONTACT=false
export ELASTICSEARCH_URL=http://localhost:9200/_bulk
export ELASTICSEARCH_SIFT_LOGS_INDEX='siftlogs-%date{yyyy.MM.dd}'
export CONTAINER_ID=so-smshandler
export HOSTNAME=seqato

export ENABLE_DE_INGESTION=true
export INGESTION_REQUEST_URL_PATH=/data/v1/async/dataextensions/key:9508EB53-293E-409A-AAA4-A16E387B68E8/rows

export SHORTURL_API_REQUEST_PATH=firebasedynamiclinks.googleapis.com/v1/shortLinks
export SHORTURL_API_KEY=AIzaSyDLx2ryhN2dsGFI0tiVz05IbKKQJXM5rvk

export REDIS_POOL_SIZE=20
export SO_CACHE_HOSTS=localhost:6379
export DUPLICATION_KEY_TTL=600
export SO_CONTACTWINDOW_TOPIC=so.contactwindow.in
export DUPLICATE_CHECK=false

CP=$(echo ../lib/*.jar | tr ' ' ':')
export CLASSPATH=$API_CONF':'$CP':'$FLOW_LOC

GRAFANA_ARGS='-javaagent:'$LIB_HOME'/jmx_prometheus_javaagent-0.12.0.jar=54321:'$API_CONF'/jmx_export.yaml'
GRAFANA_ARGS=$GRAFANA_ARGS' -Dorg.apache.camel.jmx=true -Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.port=5555'
ARGS='-Dlogback.configurationFile='$LOGBACK_XML' -DCONFIG_HOME='$API_CONF' -DLOG_HOME='$LOG_HOME' '$GRAFANA_ARGS' -jar '$FLOW_LOC'/sfmc-smshandler-0.0.1.jar'
DAEMON=/usr/lib/jvm/java-8-openjdk/bin/java

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

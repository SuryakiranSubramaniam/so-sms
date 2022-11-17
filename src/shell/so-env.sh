#! /bin/bash

# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#title           :so-env.sh
#description     :This script sets env variable
#author          :SIFT SO Team
#version         :0.0.1   
#usage           :bash so-env.sh
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

export SO_HOME=$(dirname `pwd`)
export JAVA_HOME=/usr/lib/jvm/java-8-oracle
#sift queues
export SO_SMS_INPUT_TOPIC=
export SIFT_QUEUES=siftqueue1:9092,siftqueue2:9092,siftqueue3:9092
export SO_LOG_LEVEL=debug
export LOG_TARGET=elastic

export AUTO_OFFSET_RESET=latest
export MAX_POLL_RECORDS_CONFIG=20
export MAX_POLL_INTERVAL_MS_CONFIG=60000
#URLs
export AUTH_REQUEST_URL=mc63kdf7d4l9r0c3-0njhd1ss851.auth.marketingcloudapis.com/v2/token
export SMS_REGISTER_CONTACT_REQUEST_URL=localhost:10060/runtime/api/v2/registercontact
#export SMS_REQUEST_URL_PATH=/sms/v1/messageContact/NzY6Nzg6MA/send
export SMS_REQUEST_URL_PATH=/messaging/v1/sms/messages/
#export SECRETS_PATH=/opt/knowesis/sift/orchestrator/conf/secret.properties	
export AUTH_TOKEN_REQUEST=PnHzS1wyT9hBrp3jvsPPb0rqTPCg1nyZCbwHVRUhEdUPlSWVvF51SrRJNgGFR363CfaU1J8JIYzeM+HgC6aaH0l1TNzim9KlVgApQQXHYgwjVhAA1p8UgLo8pJxAGsB60BISvdUKLqgB3BJ1lFVd5/q4BHJdGvZROOGTRnabSCx7lssYNyEdpH98a3g3NkOzJ7D5Op4uckU=
#Group Id
export GROUP_ID=smsHandler
#Seda components
export AGGREGATION_COMPLETION_TIMEOUT=100
export AGGREGATION_COMPLETION_SIZE=10
export SEDA_SMSAPI_SIZE=100
export SEDA_SMSAPI_CONCURRENT_CONSUMERS=10
export SEDA_CONTACT_POLICY_SIZE=100
export SEDA_CONTACT_POLICY_CONSUMERS=10
export SEDA_REGISTER_CONTACT_SIZE=100
export SEDA_REGISTER_CONTACT_CONCURRENT_CONSUMERS=10
export SO_WHITE_LISTED_NUMBERS=0477704537,0457412087
export COUNTRY_CODE=65
export SO_WHITELISTING_ENABLE=true
export TRIGGER_SOURCE_CORE=CORE
export TRIGGER_SOURCE_SFMC=MC
export ENABLE_REGISTER_CONTACT=true
export ELASTICSEARCH_URL=http://localhost:9200/_bulk
export ELASTICSEARCH_SIFT_LOGS_INDEX='siftlogs-%date{yyyy.MM.dd}'
export CONTAINER_ID=sfmc-smshandler
export HOSTNAME=seqato

export MESSAGE_DEFINITION_KEY=sms-def-cloud-01
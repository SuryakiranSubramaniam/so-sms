#! /bin/bash

# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#title           :so-env.sh
#description     :This script sets env variable
#author          :SIFT SO Team
#version         :0.0.1   
#usage           :bash so-env.sh
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

export SO_HOME=$(dirname `pwd`)
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
#sift queues
export SO_SMS_INPUT_TOPIC=so.sms.in
export SIFT_QUEUES=localhost:9091,localhost:9092
export SO_LOG_LEVEL=debug
export LOG_TARGET=file

export AUTO_OFFSET_RESET=latest
export MAX_POLL_RECORDS_CONFIG=20
export MAX_POLL_INTERVAL_MS_CONFIG=60000
#URLs
export AUTH_REQUEST_URL=mc63kdf7d4l9r0c3-0njhd1ss851.auth.marketingcloudapis.com/v2/token
export SMS_REGISTER_CONTACT_REQUEST_URL=localhost:10060/runtime/api/v2/registercontact
#export SMS_REQUEST_URL_PATH=/sms/v1/messageContact/NzY6Nzg6MA/send
export SMS_REQUEST_URL_PATH=/messaging/v1/sms/messages/
#export SECRETS_PATH=/opt/knowesis/sift/orchestrator/conf/secret.properties	
export AUTH_TOKEN_REQUEST=n4IHnkG9jCgZ0I+wsV8Jog8TJ4Cj0GoDIv1fUH9MCuziVPMIj+a8pEMhelaKo8dxTR5s/WsPPPQeXTgjuN9Gnk5ruZLIVa71SPfgKjzYTWDFs5Icjt5cQeK/oC0p7iAXff+QMlVPN1LfvMsF8XQJZkGRLw4yCXX9xon4U/UtCwW68iwX1bsBHDN23Y0NIrKUe0gldg9dBNQtsaHGSucHDQ==
#Group Id
export GROUP_ID=smsHandler
#Seda components
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
#export MESSAGE_DEFINITION_KEY=sms-def-cloud-01

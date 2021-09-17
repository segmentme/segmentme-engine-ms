#!/bin/sh

echo "Setting up environment variables"
. ./set_env_vars_from_secret_manager.sh
export DOCKER_REGISTRY=925575267836.dkr.ecr.us-east-1.amazonaws.com
#export SEGMENTME_DB_CONNECTOR_URI="mongodb://192.168.1.14:27017/test?readPreference=primary&appname=MongoDB%20Compass&ssl=false"
export SEGMENTME_DB_CONNECTOR_URI="mongodb+srv://segmentme-connector:segmentme-connector-password@cluster0.knky3.mongodb.net/segmentme?retryWrites=true&w=majority"
export ACCESS_SERVICE_HOST="http://access-control-service"
export ANALYSIS_SERVICE_HOST="http://analysis-service"
export MANAGEMENT_SERVICE_HOST="http://management-service"
export MEASUREMENT_SERVICE_HOST="http://measurement-service"
export CHANNEL_SERVICE_PORT=7777
export CHANNEL_SERVICE_HOST=channel-service

echo "Environment variable set"

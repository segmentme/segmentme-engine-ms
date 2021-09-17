#!/bin/sh

SECRETS=$(aws secretsmanager get-secret-value --secret-id segmentme-demo_secrets --query SecretString --output text)

 REDIS_HOST="$(jq -n "$SECRETS" | jq .REDIS_HOST)" \
  || error 'Unable to select REDIS_HOST from vault response'
export REDIS_HOST

 REDIS_PORT="$(jq -n "$SECRETS" | jq .REDIS_PORT)" \
  || error 'Unable to select REDIS_PORT from vault response'
export REDIS_PORT


 API_LB_TG="$(jq -n "$SECRETS" | jq .API_LB_TG)" \
  || error 'Unable to select API_LB_TG from vault response'
export API_LB_TG


 WEB_LB_TG="$(jq -n "$SECRETS" | jq .WEB_LB_TG)" \
  || error 'Unable to select WEB_LB_TG from vault response'
export WEB_LB_TG


AUTH0_CLIENT_ID="$(jq -n "$SECRETS" | jq .AUTH0_CLIENT_ID)" \
  || error 'Unable to select AUTH0_CLIENT_ID from vault response'
export AUTH0_CLIENT_ID

AUTH0_CLIENT_SECRET="$(jq -n "$SECRETS" | jq .AUTH0_CLIENT_SECRET)" \
  || error 'Unable to select AUTH0_CLIENT_SECRET from vault response'
export AUTH0_CLIENT_SECRET

SEGMENTME_DB_CONNECTOR_URI="$(jq -n "$SECRETS" | jq .SEGMENTME_DB_CONNECTOR_URI)" \
  || error 'Unable to select SEGMENTME_DB_CONNECTOR_URI from vault response'
export SEGMENTME_DB_CONNECTOR_URI


echo "Setting up environment variables"
export DOCKER_REGISTRY=925575267836.dkr.ecr.us-east-1.amazonaws.com
export ACCESS_SERVICE_HOST="http://access-control-service"
export ANALYSIS_SERVICE_HOST="http://analysis-service"
export MANAGEMENT_SERVICE_HOST="http://management-service"
export MEASUREMENT_SERVICE_HOST="http://measurement-service"
export CHANNEL_SERVICE_PORT=7777
export CHANNEL_SERVICE_HOST=channel-service

echo "Environment variable set"


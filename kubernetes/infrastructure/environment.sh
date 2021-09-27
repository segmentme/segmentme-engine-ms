#!/bin/sh

SECRETS=$(aws secretsmanager get-secret-value --secret-id segmentme-demo_secrets --query SecretString --output text)

 REDIS_HOST="$(jq -n "$SECRETS" | jq .REDIS_HOST)" \
  || error 'Unable to select REDIS_HOST from vault response'
echo 'export REDIS_HOST' >> $BASH_ENV

 REDIS_PORT="$(jq -n "$SECRETS" | jq .REDIS_PORT)" \
  || error 'Unable to select REDIS_PORT from vault response'
echo 'export REDIS_PORT' >> $BASH_ENV


 API_LB_TG="$(jq -n "$SECRETS" | jq .API_LB_TG)" \
  || error 'Unable to select API_LB_TG from vault response'
echo 'export API_LB_TG' >> $BASH_ENV


 WEB_LB_TG="$(jq -n "$SECRETS" | jq .WEB_LB_TG)" \
  || error 'Unable to select WEB_LB_TG from vault response'
echo 'export WEB_LB_TG' >> $BASH_ENV


AUTH0_CLIENT_ID="$(jq -n "$SECRETS" | jq .AUTH0_CLIENT_ID)" \
  || error 'Unable to select AUTH0_CLIENT_ID from vault response'
echo 'export AUTH0_CLIENT_ID' >> $BASH_ENV

AUTH0_CLIENT_SECRET="$(jq -n "$SECRETS" | jq .AUTH0_CLIENT_SECRET)" \
  || error 'Unable to select AUTH0_CLIENT_SECRET from vault response'
echo 'export AUTH0_CLIENT_SECRET' >> $BASH_ENV

SEGMENTME_DB_CONNECTOR_URI="$(jq -n "$SECRETS" | jq .SEGMENTME_DB_CONNECTOR_URI)" \
  || error 'Unable to select SEGMENTME_DB_CONNECTOR_URI from vault response'
echo 'export SEGMENTME_DB_CONNECTOR_URI' >> $BASH_ENV

echo "Setting up environment variables"
echo 'export DOCKER_REGISTRY=925575267836.dkr.ecr.us-east-1.amazonaws.com' >> $BASH_ENV
echo 'export ACCESS_SERVICE_HOST="http://access-control-service"' >> $BASH_ENV
echo 'export ANALYSIS_SERVICE_HOST="http://analysis-service"' >> $BASH_ENV
echo 'export MANAGEMENT_SERVICE_HOST="http://management-service"' >> $BASH_ENV
echo 'export MEASUREMENT_SERVICE_HOST="http://measurement-service"' >> $BASH_ENV
echo 'export CHANNEL_SERVICE_PORT=7777' >> $BASH_ENV
echo 'export CHANNEL_SERVICE_HOST=channel-service' >> $BASH_ENV
printenv

echo "Environment variable set"



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
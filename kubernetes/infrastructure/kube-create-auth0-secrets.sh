#!/bin/sh

#https://kubernetes.io/docs/concepts/containers/images/#configuring-nodes-to-authenticate-to-a-private-registry
export AUTH0_CLIENT_ID_ENC=`echo $AUTH0_CLIENT_ID|base64`
export AUTH0_CLIENT_SECRET_ENC=`echo $AUTH0_CLIENT_SECRET|base64`

envsubst < ./auth-secret.yml | kubectl apply -f -
echo "Auth0 secret created"
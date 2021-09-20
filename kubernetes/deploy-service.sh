#!/bin/sh
appname=$1
IMAGE_NAME="$DOCKER_REGISTRY/segmentme-$appname:$CIRCLE_SHA1"
export IMAGE_NAME

waitForDeploymentStart(){
    while [[ $(kubectl get pods -l app=$1 -o 'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != "True" ]]; do echo "waiting for pod $1" && sleep 1; done
}
#kubectl rollout restart deployment  analysis-service

echo "Deploy $appname"
envsubst < "$appname.yml" | kubectl apply -f -
waitForDeploymentStart appname
echo "----"



kubectl apply -f deployment.yml
kubectl rollout status deploy/"$appname"
kubectl describe svc "$appname"
#!/bin/sh
#https://kubernetes.io/docs/concepts/containers/images/#configuring-nodes-to-authenticate-to-a-private-registry
echo "Setup docker registry kube secrets"
KUBERNETES_REGISTRY=aws-ecr
DOCKER_ECR=$DOCKER_REGISTRY
DOCKER_USERNAME=AWS
DOCKER_EMAIL=vak.kondratenko@gmail.com
DOCKER_SECRET=$(aws ecr get-login-password --region us-east-1 | sed -e 's/.*-p //' -e 's/ .*$//')
kubectl delete secrets ${KUBERNETES_REGISTRY} 2> /dev/null
kubectl create secret docker-registry ${KUBERNETES_REGISTRY} \
--docker-server=${DOCKER_ECR} \
--docker-username=${DOCKER_USERNAME} \
--docker-password=${DOCKER_SECRET} \
--docker-email=${DOCKER_EMAIL}

echo "Docker registry secret created"

echo "Patch kube default serviceaccount "

kubectl patch serviceaccount default -p '{"imagePullSecrets": [{"name": "aws-ecr"}]}'

echo "Patch complete"


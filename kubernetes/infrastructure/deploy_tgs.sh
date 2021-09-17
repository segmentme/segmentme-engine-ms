#!/bin/sh

envsubst < tg-api.yml | kubectl apply -f -
envsubst < tg-web-app.yml | kubectl apply -f -
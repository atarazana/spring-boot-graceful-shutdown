#!/bin/sh

DELAY=15000

kubectl run -i --tty curl --image=curlimages/curl --restart=Never -- curl http://fruit-service:8080/setup/delay/${DELAY}
echo ""
kubectl delete pod curl
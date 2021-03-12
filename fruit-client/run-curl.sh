#!/bin/sh
#kubectl run -i --tty busybox --image=busybox -- sh
kubectl run -i --tty curl --image=curlimages/curl --restart=Never -- curl http://fruit-client:8080/api/fruits
echo ""
kubectl delete pod curl
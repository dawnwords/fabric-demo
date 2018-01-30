#!/usr/bin/env bash
version="v1"
ccName=tutorial-demo
channelName=tutorialchannel
anchorsName=anchors

echo 'Creating Channel'
peer channel create -o orderer:7050 -c ${channelName} -f config/${channelName}.tx
sleep 1
peer channel create -o orderer:7050 -c ${channelName} -f config/${anchorsName}.tx
peer channel join -b ${channelName}.block

echo 'Installing Chaincode'
peer chaincode install -n ${ccName} -p github.com/hyperledger/fabric/chaincode/ -v ${version}

echo "Instantiating Chaincode"
peer chaincode instantiate -C ${channelName} -n ${ccName}  -v ${version} -c '{"Args":["invoke","{\"a\":100, \"b\":200, \"k\": \"abc\"}"]}' -o orderer:7050

sleep 5
echo "Query Chaincode"
peer chaincode query -C ${channelName} -n ${ccName}  -v ${version} -c '{"Args":["query", "abc"]}' -o orderer:7050

echo "Done"
tail -f /dev/null
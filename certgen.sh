#!/usr/bin/env bash

channelName=tutorialchannel
anchorsName=anchors
affiliation=tutorial.ibm.com
msp=TutorialMSP
outputDir=./config
mspDir=${outputDir}/MSP

echo 'Generating Certificates'
export FABRIC_CFG_PATH=$PWD
cryptogen generate --config cryptogen.yaml --output cryptogen
configtxgen -profile OrdererOrg -outputBlock orderer.block
configtxgen -profile ${channelName} -outputCreateChannelTx ${channelName}.tx -channelID ${channelName}
configtxgen -profile ${channelName} -outputAnchorPeersUpdate ${anchorsName}.tx -channelID ${channelName} -asOrg ${msp}

ORDERER_ORG=cryptogen/ordererOrganizations/orderer.net
PEER_ORG=cryptogen/peerOrganizations/${affiliation}

CA_PATH=${PEER_ORG}/ca
TLSCA_PATH=${PEER_ORG}/tlsca
ORDERER_PATH=${ORDERER_ORG}/orderers
PEER_PATH=${PEER_ORG}/peers
USERS_PATH=${PEER_ORG}/users

mkdir -p ${mspDir}/ca
mkdir -p ${mspDir}/tlsca
mkdir -p ${mspDir}/orderer
mkdir -p ${mspDir}/peer1
mkdir -p ${mspDir}/cli
cp ${CA_PATH}/*sk  ${mspDir}/ca/ca.key
cp ${CA_PATH}/*pem  ${mspDir}/ca/ca.crt
cp ${CA_PATH}/*sk  ${mspDir}/tlsca/ca.key
cp ${CA_PATH}/*pem  ${mspDir}/tlsca/ca.crt
cp -R ${ORDERER_PATH}/orderer.orderer.net/* ${mspDir}/orderer
cp -R ${PEER_PATH}/peer1.${affiliation}/* ${mspDir}/peer1
cp -R ${USERS_PATH}/Admin@${affiliation}/* ${mspDir}/cli
for j in $(ls ${mspDir});do
    if [[ ${j} == 'ca' ]];then
        cp fabric-ca-server-config.yaml ${mspDir}/${j}
    else
        cp configtx.yaml core.yaml ${mspDir}/${j}
    fi
done
cp orderer.yaml orderer.block ${mspDir}/orderer
mv *.tx orderer.block ${outputDir}/
rm -rf cryptogen

#!/bin/bash
ARCH=x86_64
FAB_VER=1.1.0-alpha
BASE_IMG_VER=0.4.5

for img in baseos baseimage;do
	echo docker pull hyperledger/fabric-${img}:${ARCH}-${BASE_IMG_VER}
    docker pull hyperledger/fabric-${img}:${ARCH}-${BASE_IMG_VER}
done

for img in ccenv peer ca orderer tools;do
	echo docker pull hyperledger/fabric-${img}:${ARCH}-${FAB_VER}
    docker pull hyperledger/fabric-${img}:${ARCH}-${FAB_VER}
done

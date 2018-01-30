package main

import (
	"testing"
	"github.com/hyperledger/fabric/core/chaincode/shim"
)

func TestDemoChaincode_Invoke(t *testing.T) {
	cc := new(DemoChaincode)
	stub := shim.NewMockStub("DemoChaincode", cc)

	res := stub.MockInvoke("1", [][]byte{[]byte("invoke"), []byte(`{"a": 100, "b": 200, "k": "key"}`)})
	if res.Status != 200 {
		t.Fail()
	}
	res = stub.MockInvoke("2", [][]byte{[]byte("query"), []byte("key")})
	if res.Status != 200 {
		t.Fail()
	}
	if string(res.Payload) != "300" {
		t.Fail()
	}
}

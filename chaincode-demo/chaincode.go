package main

import (
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"github.com/robertkrimen/otto"
)

var logger = shim.NewLogger("demo-chaincode")

type DemoChaincode struct {
}

func (cc *DemoChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	return cc.Invoke(stub)
}

func (cc *DemoChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()
	switch function {
	case "invoke":
		return doInvoke(stub, args)
	case "query":
		return doQuery(stub, args)
	default:
		return shim.Error("unsupported function name: " + function)
	}
}

func doInvoke(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) < 1 {
		return shim.Error(fmt.Sprintf("expect at least 1 argument, got %d", len(args)))
	}
	json := args[0]
	vm := otto.New()
	vm.Set("putState", func(call otto.FunctionCall) otto.Value {
		key, err := call.Argument(0).ToString()
		if err != nil {
			panic(err.Error())
		}
		val, err := call.Argument(1).ToString()
		if err != nil {
			panic(err.Error())
		}
		logger.Info("putState:", key, val)
		stub.PutState(key, []byte(val))
		return otto.UndefinedValue()
	})
	vm.Eval(`	function add(s) { 
						var json = JSON.parse(s); 
						putState(json.k, (json.a + json.b).toString()); 
					}`)
	_, err := vm.Call("add", nil, json)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(nil)
}

func doQuery(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) < 1 {
		return shim.Error(fmt.Sprintf("expect at least 1 argument, got %d", len(args)))
	}
	val, err := stub.GetState(args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(val)
}

func main() {
	logger.SetLevel(shim.LogDebug)
	err := shim.Start(new(DemoChaincode))
	if err != nil {
		fmt.Printf("Error starting chaincode: %s", err)
	}
}

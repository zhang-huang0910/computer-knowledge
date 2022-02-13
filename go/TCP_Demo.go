package main

import (
	"fmt"
	"net"
)

func main() {
	go client()
	//监听
	listen, err := net.Listen("tcp", "127.0.0.1:8800")
	if err != nil {
		fmt.Println("err = ", err)
		return
	}
	defer listen.Close()
	//等待用户
	accept, err1 := listen.Accept()
	if err1 != nil {
		fmt.Println("err1 = ", err1)
		return
	}
	//接受用户请求
	buf := make([]byte, 1024) //1024 byte的缓存区
	read, err2 := accept.Read(buf)
	if err2 != nil {
		fmt.Println("err1 = ", err1)
		return
	}
	fmt.Println("buf = ", string(buf[:read]))
	defer accept.Close()
}
func client() {
	dial, err := net.Dial("tcp", "127.0.0.1:8800")
	if err != nil {
		fmt.Println("err = ", err)
		return
	}
	defer dial.Close()
	dial.Write([]byte("你好，客户端"))
}

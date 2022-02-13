package main

import "fmt"

type student struct {
	id   int
	name string
	sex  byte
	age  int
	addr string
	str
}
type str struct {
}

func main() {
	//数据类型 pointer struct slice array map
	//a := 123
	//fmt.Printf("i was %t\n\n", a)
	//str := student{addr: "zhangsan"}
	//fmt.Println("str is", str)
	//var arr [50]int
	//for i := range arr {
	//	fmt.Printf("arr[%d] = %v", i, arr[i])
	//}
	//fmt.Println()
	//fmt.Println((&arr)[0])
	defer fmt.Println("hello world!")
	return

}

package main

import (
	"fmt"
	"time"
)

//go 的优势并发编程
// goroutine
//全局变量
var ch = make(chan int)

func main() {
	//go newTask() //新建一个协程，新建一个任务！
	//for {
	//	fmt.Println("this is a main goroutine!")
	//	time.Sleep(time.Second)
	//}

	// runtime.Gosched Goexit GOMAXPRROCS
	//go Printer("hello")
	//go Printer("world")
	// channel 解决数据同步问题  通信解决数据共享问题 <-
	go person1()
	go person2()
	for {

	}
}
func newTask() {
	for {
		fmt.Println("this is a new goroutine!")
		time.Sleep(time.Second)
	}
}
func Printer(str string) {
	for _, date := range str {
		fmt.Printf("%c", date)
		time.Sleep(time.Second)
	}
	fmt.Printf("\n")
}
func person1() {
	Printer("hello")
	ch <- 666
}
func person2() {
	<-ch
	Printer("world")
}

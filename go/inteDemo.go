package main

import (
	"fmt"
)

//定义接口类型
type Humaner interface {
	//只用声明
	sayhi()
}
type Student struct {
	name string
	id   int
}

//student 实现了此方法
func (tmp *Student) sayhi() {
	fmt.Printf("student[%s,%d] sayhi \n", tmp.name, tmp.id)
}

func (t *Teacher) sayhi() {
	fmt.Printf("Teacher[%s,%s] sayhi\n", t.addr, t.group)
}

type MyStr string

func (m *MyStr) sayhi() {
	fmt.Printf("MyStr[%s] sayhi \n", *m)
}

type Teacher struct {
	addr  string
	group string
}

func main() {
	//定义接口类型的变量
	var i Humaner
	//实现此接口方法的类型，可以赋值
	i = &Student{"tom", 666}
	i.sayhi()
	i = &Teacher{"zhangsan", "yuwen"}
	i.sayhi()
	var str MyStr = "hello world!"
	i = &str
	i.sayhi()
}

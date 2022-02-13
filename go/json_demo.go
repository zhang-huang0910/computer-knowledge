package main

import (
	"encoding/json"
	"fmt"
)

//json成员首字母必须大写
type IT struct {
	Company  string `json:"company"`
	Subjects []string
	IsOk     bool `json:",string"`
	Price    float64
}

func main() {
	//定义一个机构体变量
	s := IT{"javazh", []string{"Go", "PHP", "Java", "Python", "Test"}, true, 6666.66}
	buf, err := json.Marshal(s)
	if err != nil {
		fmt.Println("err =", err)
		return
	}
	fmt.Println("buf = ", string(buf))
	//定义一个map
	m := make(map[string]interface{})
	m["company"] = "javazh"
	m["subjects"] = []string{"Go", "PHP", "Java", "Python", "Test"}
	m["isOk"] = false
	m["price"] = 66.66
	bytes, err1 := json.Marshal(m)
	if err1 != nil {
		fmt.Println("err =", err1)
		return
	}
	fmt.Println("bytes is ", string(bytes))
	defer fmt.Println("关闭1")
	defer fmt.Println("关闭2")
	defer fmt.Println("关闭3")
}

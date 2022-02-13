package main

import (
	"fmt"
	"io"
	"net/http"
)

//服务端编写的业务处理逻辑
func myHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Fprint(w, "hello world!")
}
func main() {
	//http.HandleFunc("/go", myHandler)
	//指定的地址进行监听，开启一个HTTP
	//http.ListenAndServe
	client := &http.Client{}
	req,_ := http.NewRequest("GET", "https://movie.douban.com/top250?start=0&filter=", nil)
	req.Header.Set("user-agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36")
	resp, err := client.Do(req)
	if err != nil {
		fmt.Println("err = ", err)
	}
	defer resp.Body.Close()
	fmt.Println("Status = ", resp.Status)
	fmt.Println("StatusCode = ", resp.StatusCode)
	fmt.Println("Header = ", resp.Header)
	defer resp.Body.Close()
	//循环读取网页数据 传出给调用者
	var result string
	buf := make([]byte, 1024)
	for {
		read, err2 := resp.Body.Read(buf)
		if read == 0 {
			//fmt.Println("读取网页完成")
			break
		}
		if err2 != nil && err2 != io.EOF {
			err = err2
			return
		}
		result += string(buf[:read])
	}
	fmt.Println("result = ",result)
}

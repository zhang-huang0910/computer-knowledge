package main

import (
	"fmt"
	"io"
	"net/http"
	"os"
	"strconv"
)

func main() {
	var start, end int
	fmt.Println("输入爬取的起始页面（》=1）：")
	fmt.Scan(&start)
	fmt.Println("输入爬取的终止页面页面（》=start）：")
	fmt.Scan(&end)
	scrabbleHandler(start, end)
}

func scrabbleHandler(start, end int) {
	fmt.Printf("正在爬取第%d页到%d...\n", start, end)
	//循环爬取每一页的内容
	// https://tieba.baidu.com/f?kw=%E7%BB%9D%E5%9C%B0%E6%B1%82%E7%94%9F&ie=utf-8&pn=0
	page := make(chan int)
	for i := start; i <= end; i++ {
		go func(i1 int, c chan int) {
			url := "https://tieba.baidu.com/f?kw=%E7%BB%9D%E5%9C%B0%E6%B1%82%E7%94%9F&ie=utf-8&pn=" + strconv.Itoa((i1-1)*50)
			result, err := HttpGet(url)
			if err != nil {
				fmt.Println("HttpGet err: ", err)
				return
			}
			//fmt.Println("result= ",result)
			//读到的数据进行保存
			f, err := os.Create("第" + strconv.Itoa(i1) + "页" + ".html")
			if err != nil {
				fmt.Println("Create err: ", err)
				return
			}
			f.WriteString(result)
			f.Close() //保存一个文件 关闭一个流
			page <- i1
		}(i, page)
	}
	for i := start; i <= end; i++ {
		fmt.Printf("第%d个页面爬取完成\n", <-page)
	}
}

func HttpGet(url string) (result string, err error) {
	resp, err1 := http.Get(url)
	resp.Header.Set("user-agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36")
	if err1 != nil {
		err = err1
	}
	defer resp.Body.Close()
	//循环读取网页数据 传出给调用者
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
	return
}

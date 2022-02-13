package main

import (
	"fmt"
	"io"
	"net/http"
	"os"
	"regexp"
	"strconv"
)

func main() {
	var start, end int
	fmt.Println("输入爬取的起始页面（》=1）：")
	fmt.Scan(&start)
	fmt.Println("输入爬取的终止页面页面（》=start）：")
	fmt.Scan(&end)
	scrabbleMovie(start, end)
}

func scrabbleMovie(start, end int) {
	fmt.Printf("正在爬取第%d页到%d...\n", start, end)
	//循环爬取每一页的内容
	// https://movie.douban.com/top250?start=0&filter=
	f, err := os.Create("豆瓣电影top250" + ".txt")
	if err != nil {
		fmt.Println("Create err: ", err)
		return
	}
	defer f.Close()
	//page := make(chan int)
	f.WriteString("排名\t\t\t电影名\t\t\t评分\t\t\t评价人数\n")
	for i := start; i <= end; i++ {
		func(i1 int) {
			url := "https://movie.douban.com/top250?start=" + strconv.Itoa((i1-1)*25) + "&filter="
			result, err := HttpGet1(url)
			//fmt.Println(url)
			if err != nil {
				fmt.Println("HttpGet err: ", err)
				return
			}
			//fmt.Println("result= ", result)
			//读到的数据进行保存
			//f, err := os.Create("第" + strconv.Itoa(i1) + "页" + ".txt")
			filterText(i, f, result)
			//page <- i1
		}(i)
	}
	//for i := start; i <= end; i++ {
	//	fmt.Printf("第%d个页面爬取完成\n", <-page)
	//}
}

func filterText(page int, f *os.File, result string) {
	name := regexp.MustCompile(`<img width="100" alt="(.*?)"`)
	score := regexp.MustCompile(`<span class="rating_num" property="v:average">(.*?)</span>`)
	comment := regexp.MustCompile(`<span>(.*?)人评价</span>`)
	movieName := name.FindAllStringSubmatch(result, -1)
	scoreName := score.FindAllStringSubmatch(result, -1)
	commentName := comment.FindAllStringSubmatch(result, -1)
	for i := 0; i < len(movieName); i++ {
		f.WriteString(strconv.Itoa(i+1+(page-1)*25) + "\t\t\t" + movieName[i][1] + "\t\t\t" + scoreName[i][1] + "\t\t\t" + commentName[i][1] + "\n")
	}
}

func HttpGet1(url string) (result string, err error) {
	client := &http.Client{}
	req, _ := http.NewRequest("GET", url, nil)
	req.Header.Set("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36")
	resp, err1 := client.Do(req)
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

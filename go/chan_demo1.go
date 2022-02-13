package main

import (
	"fmt"
	"time"
)

func main() {
	//无缓存俄chan
	ch := make(chan int, 0)
	//fmt.Printf("len(ch) = %d, cap(ch) = %d\n", len(ch), cap(ch))

	go func() {
		for i := 0; i < 3; i++ {
			ch <- i
			fmt.Printf("len(ch) = %d, cap(ch) = %d\n", len(ch), cap(ch))
		}
	}()
	for i := 0; i < 3; i++ {
		num := <-ch
		fmt.Println("num = ", num)
	}
	//有缓存的channel
	//关闭chan close(ch)
	//time ticker
	time.Now()
	time.NewTimer(time.Second)
	time.NewTimer(time.Second)

}

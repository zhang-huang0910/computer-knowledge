package main

import (
	"fmt"
	"io"
	"os"
)

func main() {
	list := os.Args
	if len(list) != 3 {
		fmt.Println("please give srcFile and destFile!")
		return
	}
	srcFile := list[1]
	destFile := list[2]
	sf, err1 := os.Open(srcFile)
	df, _ := os.Create(destFile)
	if srcFile == destFile {
		fmt.Println("the destination fileName is same, please reset the name!", err1)
		return
	}
	if err1 != nil {
		fmt.Println("file is not exit or is only read! err1 = ", err1)
		return
	}
	defer sf.Close()
	defer df.Close()
	buf := make([]byte, 4*1024)
	for {
		read, err2 := sf.Read(buf)
		if err2 != nil && err2 == io.EOF {
			break
		}
		df.Write(buf[:read])
	}
}

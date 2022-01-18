package com.javazh.nio.chat;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * @auther zhz
 * @Date 2022-01-18 01:16
 */
public class ChatClient {
    public void start(String name) throws Exception {
        // socketChannel
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(1888));
        System.out.println(" client start ");
        socketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_READ);
        //创建线程处理
        new Thread(new ClientThread(selector)).start();
        // send message
        Scanner scanner = new Scanner(System.in);
        String msg = null;
        while (scanner.hasNextLine()) {
            msg = scanner.nextLine();
            if (msg != null && msg.length() > 0) {
                socketChannel.write(Charset.forName("UTF-8").encode(name + ": " + msg));
            }
        }
    }

}

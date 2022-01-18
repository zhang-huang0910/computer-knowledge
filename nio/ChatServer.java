package com.javazh.nio.chat;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 * @auther zhz
 * @Date 2022-01-18 00:27
 */
public class ChatServer {
    public void start() throws Exception {
        // select
        Selector selector = Selector.open();
        // channel
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.bind(new InetSocketAddress(1888));
        //register
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器已经启动");
        //处理
        for (; ; ) {
            int num = selector.select();
            if (num == 0) {
                continue;
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                //根据状态来确定
                if (key.isAcceptable()) {
                    acceptOperator(socketChannel, selector);
                }
                if (key.isReadable()) {
                    //todo something
                    try {
                        readOperator(selector, key);
                    } catch (Exception e) {
                        System.out.println("client exit！");
                    }

                }

            }
        }
    }

    //处理可读状态的操作
    private void readOperator(Selector selector, SelectionKey key) throws Exception {
        //channel
        SocketChannel channel = (SocketChannel) key.channel();
        //buffer
        ByteBuffer buffer = ByteBuffer.allocate(50);
        //loop
        int nums = channel.read(buffer);
        byte[] str = null;
        while (nums > 0) {
            buffer.flip();
            byte[] bytes = Arrays.copyOfRange(buffer.array(), 0, nums);
            if (str == null) {
                str = bytes;
            } else {
                str = Arrays.copyOf(str, str.length + nums);
                System.arraycopy(bytes, 0, str, str.length - nums, nums);
            }
            buffer.clear();
            nums = channel.read(buffer);
        }

        //registry
        channel.register(selector, SelectionKey.OP_READ);
        //broadcast
        if (str != null && str.length > 0) {
            // to other channel
            String str1 = new String(str, Charset.forName("UTF-8"));
            System.out.println(str1);
            broadcastToOtherClients(str1, selector, channel);
        }
    }

    private void broadcastToOtherClients(String message, Selector selector, SocketChannel channel) throws Exception {
        Set<SelectionKey> keys = selector.keys();
        if (keys.size() < 2) {
            return;
        }
        for (SelectionKey key : keys) {
            Channel socketChannel = key.channel();
            if (socketChannel instanceof SocketChannel && socketChannel != channel) {
                ((SocketChannel) socketChannel).write(Charset.forName("UTF-8").encode(message));
            }
        }
    }

    //处理可接受的状态
    private void acceptOperator(ServerSocketChannel socketChannel, Selector selector) throws Exception {
        //接入socketChannel
        SocketChannel channel = socketChannel.accept();
        //socket set no blocking
        channel.configureBlocking(false);
        //channel注册
        channel.register(selector, SelectionKey.OP_READ);
        //客户端回复信息
        channel.write(Charset.forName("UTF-8")
                .encode("欢迎进入聊天室，请注意隐私安全"));
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

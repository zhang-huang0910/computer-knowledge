package com.javazh.nio.chat;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 * @auther zhz
 * @Date 2022-01-18 21:52
 */
public class ClientThread implements Runnable {
    private Selector selector;

    public ClientThread(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
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
        } catch (Exception e) {

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
        }
    }
}

package com.blue;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author zhangh <br/>
 * @date 2023-03-14 11:50
 **/
public class DownLoadImage {
    private final String joinName;
    private final String fileName;
    private final String suffix;
    private final String delimiter;
    private final List<String> urlList;
    private static final Pattern reg = Pattern.compile("(https*:\\/\\/.+?(.png))");

    public DownLoadImage(String[] args) {
        this.fileName = args[0];
        this.joinName = args[1];
        this.suffix = args[2];
        this.delimiter = System.getProperty("file.separator");
        this.urlList = urlList(fileName);
    }

    public List<String> urlList(String pathFile) {
        FileReader file = null;
        BufferedReader br = null;
        List<String> urlList = new ArrayList<>();
        try {
            //new URI()
            file = new FileReader(fileName);//读取文件流位置
            br = new BufferedReader(file);//构造一个BufferedReader类读取文件
            String document;
            while ((document = br.readLine()) != null) {
                Matcher m = reg.matcher(document);
                while (m.find()) {
                    urlList.add(m.group());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return urlList;
    }

    public String filepath(String file) {
        File file1 = new File(file);
        if (file1.isDirectory()) {
            return file;
        } else {
            return file1.getParent();
        }
    }

    private void downImage(String fileUrl, String fileName) {
        URL url = null;
        try {
            url = new URL(fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        File file = new File(fileName);
        try (
                InputStream inputStream = url.openConnection().getInputStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                FileOutputStream op = new FileOutputStream(file);
        ) {
            byte[] buf = new byte[1024 * 4];
            int len;
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            op.write(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> simpleImageNames(List<String> list) {
        return list.stream()
                .map(e -> {
                    int index = e.lastIndexOf("/");
                    String filename = e.substring(index + 1);
                    int suffix = filename.lastIndexOf(".");
                    return filename.substring(0, suffix);
                })
                .collect(Collectors.toList());
    }

    private void createFile() {
        File filePath = new File(filepath(fileName) + System.getProperty("file.separator") + joinName);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
    }

    private void batchDownImages(List<String> suffixFileName) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < urlList.size(); i++) {
            String newFileName = this.filepath(fileName) + delimiter + suffixFileName.get(i);
            downImage(urlList.get(i), newFileName);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("batchDownImages run time: " + (endTime - startTime) + "ms");
    }

    private List<String> newSuffixFileName(Boolean useSimpleName) {
        List<String> simpleImageNames = null;
        if (useSimpleName) {
            simpleImageNames = simpleImageNames(urlList);
        } else {
            simpleImageNames = IntStream.range(1, urlList.size() + 1).mapToObj(String::valueOf).collect(Collectors.toList());
        }
        return simpleImageNames.stream().map(e -> joinName + delimiter + e + suffix).collect(Collectors.toList());
    }

    private void replaceUrlForFile(List<String> oldUrl, List<String> newUrl, String fileName) {
        HashMap<String, String> hashMap = new HashMap<>(oldUrl.size());
        for (int i = 0; i < oldUrl.size(); i++) {
            hashMap.put(oldUrl.get(i), newUrl.get(i));
        }
        try {
            //读取文件并替换
            File file = new File(fileName);
            FileReader fileReader = new FileReader(file);
            //String newFileName = fileName.substring(0, fileName.lastIndexOf("md")) + "tmp";
            //File newFile = new File(newFileName);

            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String document;
            StringBuffer buffer = new StringBuffer();
            while ((document = bufferedReader.readLine()) != null) {
                Matcher m = reg.matcher(document);
                while (m.find()) {
                    document = document.replace(m.group(), hashMap.get(m.group()));
                }
                buffer.append(document);
                buffer.append("\n");
            }
            bufferedReader.close();
            fileReader.close();
            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
            bufferWriter.write(buffer.toString());
            bufferWriter.close();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        String fileName = "C:\\Users\\blue\\Downloads\\Consumer源码分析-笔记.md";
        String[] str = new String[]{"C:\\Users\\blue\\Downloads\\Consumer源码分析-笔记.md", "jpg\\consumer", ".png"};
        DownLoadImage downLoadImage = new DownLoadImage(str);
        //downLoadImage.simpleImageNames(downLoadImage.urlList(fileName)).forEach(System.out::println);
        downLoadImage.createFile();
        List<String> newSuffixFileName = downLoadImage.newSuffixFileName(true);
        if (newSuffixFileName == null || newSuffixFileName.size() == 0) {
            return;
        }
        try {
            downLoadImage.replaceUrlForFile(downLoadImage.urlList, newSuffixFileName, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        CountDownLatch downLatch = new CountDownLatch(2);
        //加载图片
        new Thread(() -> {
            downLoadImage.batchDownImages(newSuffixFileName);
            downLatch.countDown();
        }).start();
        //修改文件路径
        new Thread(() -> {
            downLoadImage.replaceUrlForFile(downLoadImage.urlList, newSuffixFileName, fileName);
            downLatch.countDown();
        }).start();
        try {
            downLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("main run time: " + (endTime - startTime) + "ms");
    }

}

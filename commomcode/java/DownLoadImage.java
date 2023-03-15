package com.blue;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author zhangh <br/>
 * @date 2023-03-14 11:50
 **/
public class DownLoadImage {
    private final String joinName;
    private final String fileName;
    private final String suffix;
    private final String delimiter = System.getProperty("file.separator");
    private final List<String> urlList;
    private static final Pattern reg = Pattern.compile("\\(https*:\\/\\/.+?(.png)\\)");

    public DownLoadImage(List<String> args) {
        this.fileName = args.get(0);
        this.joinName = args.get(1);
        this.suffix = args.get(2);
        this.urlList = urlList(fileName);
    }

    public List<String> urlList(String pathFile) {
        FileReader file = null;
        BufferedReader br = null;
        List<String> urlList = new ArrayList<>();
        try {
            //new URI()
            file = new FileReader(pathFile);//读取文件流位置
            br = new BufferedReader(file);//构造一个BufferedReader类读取文件
            String document;
            while ((document = br.readLine()) != null) {
                Matcher m = reg.matcher(document);
                while (m.find()) {
                    urlList.add(m.group().substring(1, m.group().length() - 1));
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

    private String simpleImageName(String fileStr) {
        int index = fileStr.lastIndexOf("/");
        String filename = fileName.substring(index + 1);
        int suffix = filename.lastIndexOf(".");
        return filename.substring(0, suffix);
    }

    private void createFile() {
        File filePath = new File(filepath(fileName) + System.getProperty("file.separator") + joinName);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
    }

    private void batchDownImages(Map<String, String> originAndNewFileName) {
        originAndNewFileName.entrySet().forEach(entry -> {
            String newFileName = this.filepath(fileName) + delimiter + entry.getValue();
            downImage(entry.getKey(), newFileName);
        });
    }

    private Map<String, String> newSuffixFileName(Boolean useSimpleName) {
        HashMap<String, String> originAndNewFileName = new HashMap<>();
        int flag = 1;
        for (String url : urlList) {
            if (!originAndNewFileName.containsKey(url)) {
                String value;
                if (useSimpleName) {
                    value = simpleImageName(url);
                } else {
                    value = String.valueOf(flag++);
                }
                if (value == null) continue;
                originAndNewFileName.put(url, joinName + delimiter + value + suffix);
            }
        }
        return originAndNewFileName;
    }

    private void replaceUrlForFile(Map<String, String> originAndNewFileName, String fileName) {
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
                    String key = m.group().substring(1, m.group().length() - 1);
                    document = document.replace(key, originAndNewFileName.get(key));
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

    private static List<String> getArgs(String str) {
        File file = new File(str);
        List<String> args = new ArrayList<>();
        args.add(0, str);
        args.add(1, "jpg\\" + file.getName().substring(0, 2));
        args.add(2, ".png");
        return args;
    }

    private static void batchModifyFileImages(String str) {
        DownLoadImage downLoadImage = new DownLoadImage(getArgs(str));
        //downLoadImage.simpleImageNames(downLoadImage.urlList(fileName)).forEach(System.out::println);
        downLoadImage.createFile();
        Map<String, String> newSuffixFileName = downLoadImage.newSuffixFileName(false);
        if (newSuffixFileName == null || newSuffixFileName.size() == 0) {
            return;
        }
        //加载图片
        downLoadImage.batchDownImages(newSuffixFileName);
        //修改文件路径
        downLoadImage.replaceUrlForFile(newSuffixFileName, downLoadImage.fileName);

    }


    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        String orinName = "";
        File file = new File(orinName);
        if (args != null && args.length == 1) {
            orinName = args[0];
        }
        if (orinName == null || orinName.trim().isEmpty()) {
            System.out.println("No args or invalid args...");
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles(fil -> fil.getName().matches(".*\\.md"));
            Stream.of(files).forEach(fi -> batchModifyFileImages(fi.getAbsolutePath()));
        } else {
            if (file.isFile()) {
                batchModifyFileImages(orinName);
            } else {
                System.out.println("invalid filePath...");
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("main run time: " + (endTime - startTime) + "ms");
    }
}

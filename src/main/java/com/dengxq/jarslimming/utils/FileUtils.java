package com.dengxq.jarslimming.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class FileUtils {

    /**
     * 获取class 文件的字节数组
     *
     * @param path
     * @return
     */
    public static byte[] getClassData(String path) {
        InputStream iStream = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            iStream = new FileInputStream(path);

            byte[] buffer = new byte[1024];
            int temp = 0;
            while ((temp = iStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, temp);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (iStream != null) {
                    iStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 源码写入到指定包指定后缀的源文件中。
     *
     * @param name 表信息
     * @param src  源代码
     */
    public static void createFile(String name, String src) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(name));
            bw.write(src);
            bw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 解压文件
     *
     * @param filePath
     * @return 文件父文件目录
     */
    public static String unzipFile(String filePath) {
        File file = new File(filePath);//压缩文件
        try {
            ZipFile zipFile = new ZipFile(file);//实例化ZipFile，每一个zip压缩文件都可以表示为一个ZipFile
            //实例化一个Zip压缩文件的ZipInputStream对象，可以利用该类的getNextEntry()方法依次拿到每一个ZipEntry对象
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file), Charset.forName("GBK"));
            ZipEntry zipEntry = null;
            filePath = file.getCanonicalPath();
            String path = filePath.substring(0, filePath.lastIndexOf(".")) + "/";
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.isDirectory()) {
                    String name = zipEntry.getName();
                    name = name.substring(0, name.length() - 1);
                    File f = new File(path + name);
                    f.mkdirs();
                } else {
                    File f = new File(path + zipEntry.getName());
                    f.getParentFile().mkdirs();
                    f.createNewFile();
                    InputStream is = zipFile.getInputStream(zipEntry);
                    FileOutputStream fos = new FileOutputStream(f);
                    int length = 0;
                    byte[] b = new byte[1024];

                    while ((length = is.read(b, 0, 1024)) != -1) {
                        fos.write(b, 0, length);
                    }
                    is.close();
                    fos.close();
                }
            }
            zipInputStream.close();
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 复制文件
     *
     * @param fromFile
     * @param toFile
     * @throws IOException
     */
    public static void copyFile(String fromFile, String toFile) {
        File inFile = new File(fromFile);
        if (!inFile.exists()) {
            return;
        }

        try {
            FileInputStream ins = new FileInputStream(new File(fromFile));
            File outFile = new File(toFile);
            outFile.getParentFile().mkdirs();
            outFile.createNewFile();
            FileOutputStream out = new FileOutputStream(outFile);
            byte[] b = new byte[1024];
            int n = 0;
            while ((n = ins.read(b)) != -1) {
                out.write(b, 0, n);
            }

            ins.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyDir(String sourcePath, String newPath, Boolean filterClass) {
        String[] filePaths = (new File(sourcePath)).list();
        if (filePaths == null) {
            return;
        }

        if (!(new File(newPath)).exists()) {
            (new File(newPath)).mkdirs();
        }

        for (String filePath : filePaths) {
            File file = new File(sourcePath + File.separator + filePath);
            if (file.isDirectory()) {
                copyDir(sourcePath + File.separator + filePath, newPath + File.separator + filePath, filterClass);
            } else if (file.isFile()) {
                if (filterClass && !filePath.endsWith(".class")) {
                    copyFile(sourcePath + File.separator + filePath, newPath + File.separator + filePath);
                }
            }
        }
    }

    //删除文件夹
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            File myFilePath = new File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //删除指定文件夹下的所有文件
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + File.separator + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + File.separator + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

}

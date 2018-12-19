package com.dengxq.jarslimming.utils;

import java.io.*;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtils {

    private static final String SRC_JAR_PATH = "Src-Path";
    private static final String SRC_JAR_MODIFY_TIME = "Src-Modify-Time";

    /**
     * 从zip文件中读取预留信息
     *
     * @param jarPath zip文件
     * @return 预留信息
     */
    public static String readInfoFromJar(String jarPath) {
        try {
            JarFile jarFile = new JarFile(jarPath);
            Attributes atr = jarFile.getManifest().getMainAttributes();
            String info = atr.getValue(SRC_JAR_PATH) + atr.getValue(SRC_JAR_MODIFY_TIME);
            jarFile.close();
            return info;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readMainClassFromJar(String jarPath) {
        try {
            JarFile jarFile = new JarFile(jarPath);
            Attributes atr = jarFile.getManifest().getMainAttributes();
            String mainClass = atr.getValue(Attributes.Name.MAIN_CLASS);
            jarFile.close();
            return mainClass;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从zip文件中读取某一个文件的字节流
     *
     * @param zipFile  zip文件
     * @param fileName 读取文件路径
     * @return 字节流
     */
    public static byte[] getBytesFromZipFile(String zipFile, String fileName) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ZipFile zf = new ZipFile(zipFile);
            ZipEntry ze = zf.getEntry(fileName);
            if (ze != null) {
                InputStream in = zf.getInputStream(ze);
                byte[] buffer = new byte[1024];
                int temp = 0;
                while ((temp = in.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, temp);
                }
                return byteArrayOutputStream.toByteArray();
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据过滤策略删除部分文件，并生成新jar
     *
     * @param jarName       原始jar
     * @param outputJarName 输出jar
     * @param retains       保留策略
     * @return 删除文件个数
     */
    public static int generateJar(String jarName, String outputJarName, Set<String> retains, String mainClass) {
        int deleteFileCount = 0;
        try {
            JarFile jarFile = new JarFile(jarName);
            Manifest manifest = jarFile.getManifest();
            Attributes atr = manifest.getMainAttributes();
            atr.putValue(SRC_JAR_PATH, jarName);
            atr.putValue(SRC_JAR_MODIFY_TIME, "" + (new File(jarName)).lastModified());
            atr.put(Attributes.Name.MAIN_CLASS, mainClass);

            JarOutputStream jos = new JarOutputStream(new FileOutputStream(outputJarName), manifest);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (!name.endsWith(".class") && !name.equals("META-INF/MANIFEST.MF") || retains.contains(name)) {
                    InputStream inputStream = jarFile.getInputStream(entry);
                    jos.putNextEntry(entry);
                    byte[] bytes = readStream(inputStream);
                    jos.write(bytes, 0, bytes.length);
                } else {
                    deleteFileCount++;
                }
            }

            jos.flush();
            jos.finish();
            jos.close();
            jarFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return deleteFileCount;
    }

    private static byte[] readStream(InputStream inStream) throws IOException {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        return outSteam.toByteArray();
    }
}

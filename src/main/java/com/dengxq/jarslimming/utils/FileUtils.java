package com.dengxq.jarslimming.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtils {

    private static final String lastGenFileName = "lastGenInfo";

    public static String readInfoFromZipFile(String zipFile) {
        try {
            ZipFile zf = new ZipFile(zipFile);
            ZipEntry ze = zf.getEntry(lastGenFileName);
            if (ze != null) {
                return ze.getComment();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static int deleteFromJar(String jarName, String outputJarName, Set<String> retains) {
        int deleteFileCount = 0;
        try {
            //创建文件（根据备份文件并删除部分）
            JarFile jarFile = new JarFile(jarName);
            JarOutputStream jos = new JarOutputStream(new FileOutputStream(outputJarName));
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (!name.endsWith(".class") || retains.contains(name)) {
                    InputStream inputStream = jarFile.getInputStream(entry);
                    jos.putNextEntry(entry);
                    byte[] bytes = readStream(inputStream);
                    jos.write(bytes, 0, bytes.length);
                } else {
                    deleteFileCount ++;
                }
            }
            //写入生成文件信息，下次运行时判断是否需要生成
            String content = jarName + (new File(jarName)).lastModified();
            ZipEntry properFile = new ZipEntry(lastGenFileName);
            properFile.setComment(content);
            jos.putNextEntry(properFile);

            jos.flush();
            jos.finish();
            jos.close();
            jarFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deleteFileCount;
    }

    private static byte[] readStream(InputStream inStream) throws Exception {
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

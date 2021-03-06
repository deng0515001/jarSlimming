package com.dengxq.jarslimming;

import com.dengxq.jarslimming.core.GetImport;
import com.dengxq.jarslimming.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 入口类
 */
public class JarSlimming {

    /**
     * @param args 输入参数0： 原始jar文件     必填
     *             输入参数1： 入口class      选填
     *             输入参数2： 生成jar文件路径 选填
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Useage:java -jar jarSlimming-1.0.jar <input_jarPath> <input_classPath> ");
            System.exit(1);
        }
        String jarPath = args[0]; //jar包原始路径
        File srcJar = new File(jarPath);
        if (!jarPath.contains(".jar") || !srcJar.exists() || !srcJar.isFile()) {
            System.err.println(jarPath + " is not a correct jar file");
            System.exit(1);
        }
        try {
            jarPath = srcJar.getCanonicalPath();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        String baseDir = (new File(jarPath)).getParent();

        String mainClass;
        if (args.length > 1) {
            mainClass = args[1]; //输入根节点
        } else {
            mainClass = FileUtils.readMainClassFromJar(jarPath);
        }
        if (mainClass == null || mainClass.length() < 2) {
            System.err.println("Useage:java -jar jarSlimming-1.0.jar <input_jarPath> <input_classPath> ");
            System.exit(1);
        }

        //输出文件路径可选
        String outputJar;
        if (args.length > 2) {
            outputJar = args[2];
        } else {
            String rootClassName = mainClass.substring(mainClass.lastIndexOf(".") + 1);//根文件class名
            outputJar = baseDir + File.separator + rootClassName + ".jar";
        }

        //判断输出文件是否存在，存在则判断生成文件是否发生变化
        if ((new File(outputJar)).exists()) {
            String lastInfo = FileUtils.readInfoFromJar(outputJar);
            String newInfo = jarPath + srcJar.lastModified();
            if (newInfo.equals(lastInfo)) {
                System.out.println(outputJar + " exists and src file not modify, exit");
                System.out.println("output: " + outputJar);
                System.exit(0);
            } else {
                System.out.println(outputJar + " modified, continue");
            }
        }

        System.out.println("start to analyse jar");
        Set<String> dependClasses = getAllDependClasses(jarPath, mainClass);

        System.out.println("start to delete unused classes");
        int deleteFileCount = FileUtils.generateJar(jarPath, outputJar, dependClasses, mainClass);
        System.out.println("total delete " + deleteFileCount + " unused class");
        System.out.println("output: " + outputJar);
    }

    /**
     * 获取所有依赖class
     *
     * @param mainClass 依赖文件(包)路径 根
     * @param jarPath   jar包路径
     * @return
     */
    private static Set<String> getAllDependClasses(String jarPath, String mainClass) {
        Stack<String> stack = new Stack<>();
        String path = mainClass.replace(".", File.separator);
        try {
            ZipFile zf = new ZipFile(jarPath);
            Enumeration<? extends ZipEntry> e = zf.entries();
            while (e.hasMoreElements()) {
                String name = e.nextElement().getName();
                if (name.startsWith(path) && name.endsWith(".class")) {
                    stack.push(name);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<String> resultFiles = new HashSet<>();
        while (!stack.isEmpty()) {
            String filePath = stack.pop();
            Set<String> set = GetImport.getImports(jarPath, filePath);
            for (String item : set) {
                if (!resultFiles.contains(item)) {
                    stack.push(item);
                }
            }
            resultFiles.add(filePath);
        }
        return resultFiles;
    }
}

package com.dengxq.jarslimming;

import com.dengxq.jarslimming.core.GetImport;
import com.dengxq.jarslimming.utils.FileUtils;
import com.dengxq.jarslimming.utils.ZipUtils;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * 入口类
 */
public class JarSlimming {

    private static final String lastGenFileName = "lastGenInfo";

    /**
     *
     * @param args 输入参数0： 原始jar文件     必填
     *             输入参数1： 入口class      必填
     *             输入参数2： 生成jar文件路径 选填
     */
    public static void main(String[] args) {
        if (args.length < 2) {
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
        } catch ( Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        String baseDir = (new File(jarPath)).getParent();

        String rootClassPath = args[1]; //输入根节点
        String rootClassName = rootClassPath.substring(rootClassPath.lastIndexOf(".") + 1);//根文件class名

        //输出文件路径可选
        String outputJar;
        if (args.length > 2) {
            outputJar = args[2];
        } else {
            outputJar = baseDir + File.separator + rootClassName + ".jar";
        }

        //判断输出文件是否存在，存在则判断生成文件是否发生变化
        if ((new File(outputJar)).exists()) {
            String lastGenInfo = FileUtils.readFromZipFile(outputJar, lastGenFileName);
            String newInfo = jarPath + srcJar.lastModified();
            if (newInfo.equals(lastGenInfo)) {
                System.out.println(rootClassName + ".jar exists, exit");
                System.out.println("output: " + outputJar);
                System.exit(0);
            } else {
                System.out.println("newInfo = " + newInfo);
                System.out.println("lastGenInfo = " + lastGenInfo);
                System.out.println(rootClassName + ".jar modified, continue");
            }
        }

        String copyFileDir = baseDir + File.separator + rootClassName + File.separator;
        run(jarPath, rootClassName, rootClassPath, copyFileDir, outputJar);
    }

    private static void run(String jarPath, String rootClassName, String rootClassPath, String copyFileDir, String outputJar) {
        File srcJar = new File(jarPath);

        System.out.println("start to analyse jar");
        String unzipJarPath = jarPath.substring(0, jarPath.lastIndexOf(".")) + System.currentTimeMillis() + "/" ; //jar包解压路径
        FileUtils.unzipFile(srcJar, unzipJarPath);
        Set<String> dependClasses = getAllDependClasses(rootClassName, rootClassPath, unzipJarPath);

        // 拷贝所有依赖class
        System.out.println("start to copy class files");
        int len = unzipJarPath.length();
        for (String string : dependClasses) {
            String copyPath = copyFileDir + string.substring(len);
            FileUtils.copyFile(string, copyPath);
        }

        // 拷贝资源文件
        System.out.println("start to copy resource files");
        FileUtils.copyDir(unzipJarPath, copyFileDir, true);
        FileUtils.delFolder(unzipJarPath);

        //写入生成文件信息，下次运行时判断是否需要生成
        String properFile = copyFileDir + lastGenFileName;
        String content = jarPath + "\n" + srcJar.lastModified();
        FileUtils.saveToFile(properFile, content);

        System.out.println("start to generate new jar");
        ZipUtils.doCompress(copyFileDir, outputJar);
        FileUtils.delFolder(copyFileDir);

        System.out.println("output: " + outputJar);
    }

    /**
     * 获取所有依赖class
     * @param className 依赖文件名 根
     * @param rootClassPath 依赖文件(包)路径 根
     * @param unzipJarPath jar包解压的根路径
     * @return
     */
    private static Set<String> getAllDependClasses(String className, String rootClassPath, String unzipJarPath) {
        Stack<String> stack = new Stack<>();
        String classDir =  unzipJarPath + rootClassPath.replace(".", "/");
        File classDirFile = new File(classDir);
        if (classDirFile.isDirectory()) {
            List<String> files = FileUtils.getAllFiles(classDir);
            if (files != null && files.size() > 0) {
                for (String file : files) {
                    if (file.endsWith(".class")) {
                        stack.push(file);
                    }
                }
            }
        } else {
            String rootPackage = classDirFile.getParent();
            //列出所有主类生成的class文件
            String[] files = classDirFile.getParentFile().list();
            if (files != null && files.length > 0) {
                for (String file : files) {
                    if (file.startsWith(className) && file.endsWith(".class")) {
                        String path = rootPackage + File.separator + file;
                        stack.push(path);
                    }
                }
            }
        }

        Set<String> resultFiles = new HashSet<>();
        while (!stack.isEmpty()) {
            String filePath = stack.pop();
            if ((new File(filePath)).exists()) {
                Set<String> set = GetImport.getImports(filePath, unzipJarPath);
                for (String item : set) {
                    if (!resultFiles.contains(item)) {
                        stack.push(item);
                    }
                }
                resultFiles.add(filePath);
            }
        }
        return resultFiles;
    }
}

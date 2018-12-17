package com.dengxq.jarslimming;

import com.dengxq.jarslimming.core.GetImport;
import com.dengxq.jarslimming.utils.FileUtils;
import com.dengxq.jarslimming.utils.ZipUtils;

import java.io.File;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;


public class JarSlimming {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Useage:java -jar jarSlimming-1.0.jar <input_jarPath> <input_classPath> ");
            System.exit(1);
        }
        String jarName = args[0]; //jar包原始路径
        String rootClassPath = args[1]; //输入根节点
        String rootClassName = rootClassPath.substring(rootClassPath.lastIndexOf(".") + 1);//根文件class名

        String rootPath = (new File(jarName)).getParent();
        try {
            rootPath = new File(rootPath).getCanonicalPath();
        } catch ( Exception e) {
            e.printStackTrace();
        }
        String copyFilepath = rootPath + File.separator + rootClassName;
        String outputJar = copyFilepath + ".jar";

        //判断输出文件是否存在，存在则询问是否覆盖。
        if ((new File(outputJar)).exists()) {
            System.err.print(rootClassName + ".jar exists, continue? (Y or N)");
            Scanner scan = new Scanner(System.in);
            String str = scan.next();  // 接收数据
            while (!"n".equals(str.toLowerCase()) && !"y".equals(str.toLowerCase())) {
                System.out.println("Y to continue, N to exit." + str);
                str = scan.next();
            }
            if ("n".equals(str.toLowerCase())) {
                System.exit(1);
            }
        }

        run(jarName, rootClassName, rootClassPath, copyFilepath, outputJar);
    }

    private static void run(String jarName, String rootClassName, String rootClassPath, String copyFilepath, String outputJar) {
        System.out.println("start to analyse classes");
        String unzipJarPath = FileUtils.unzipFile(jarName); //jar包解压路径
        Set<String> dependClasses = getAllDependClasses(rootClassName, rootClassPath, unzipJarPath);

        // 拷贝所有依赖class
        System.out.println("start to copy class files");
        String copyFileDir = copyFilepath + File.separator;
        for (String string : dependClasses) {
            String copyPath = copyFileDir + string.substring(unzipJarPath.length());
            FileUtils.copyFile(string, copyPath);
        }

        // 拷贝资源文件
        System.out.println("start to copy resource files");
        FileUtils.copyDir(unzipJarPath, copyFileDir, true);
        FileUtils.delFolder(unzipJarPath);

        System.out.println("start to generate new jar");
        ZipUtils.doCompress(copyFileDir, outputJar);
        FileUtils.delFolder(copyFileDir);

        System.err.println("output: " + outputJar);
    }

    /**
     * 获取所有依赖class
     * @param className 依赖文件名 根
     * @param rootClassPath 依赖文件路径 根
     * @param unzipJarPath jar包解压的根路径
     * @return
     */
    private static Set<String> getAllDependClasses(String className, String rootClassPath, String unzipJarPath) {
        String rootPackage = unzipJarPath + rootClassPath.substring(0, rootClassPath.lastIndexOf(".")).replace(".", "/");

        Stack<String> stack = new Stack<>();
        Set<String> resultFiles = new HashSet<>();
        //列出所有主类生成的class文件
        String[] files = (new File(rootPackage)).list();
        if (files != null && files.length > 0) {
            for (String file : files) {
                if (file.startsWith(className) && file.endsWith(".class")) {
                    String path = rootPackage + File.separator + file;
                    stack.push(path);
                }
            }
        }

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

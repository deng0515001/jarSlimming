package com.dengxq.jarslimming.core;

import com.dengxq.jarslimming.constant.Constant;
import com.dengxq.jarslimming.utils.FileUtils;
import com.dengxq.jarslimming.utils.HexUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 获取类的import列表
 */
public class GetImport {

    private static int start_pointer = 0;
    private static String hexString = ""; // 十六进制数据总串

    /**
     * 获取import列表
     * @param jarPath jar包路径
     * @param classPath 输入类路径
     * @return 输入类的所有class依赖，可能会存在冗余
     */
    public static Set<String> getImports(String jarPath, String classPath) {
        start_pointer = 0;

        Set<String> fileSet = new HashSet<>();
        byte[] data = FileUtils.getBytesFromZipFile(jarPath, classPath);
        if (data == null) {
            return fileSet;
        }

        hexString = HexUtil.byte2HexStr(data);

        // 1.魔数 2.jvm 次版本 3.jvm 主版本
        String magic = cutString(8);
        // 4.常量池个数
        int cpCount = Integer.parseInt(cutString(2), 16);
        // 5.常量池
        List<String> constantPoolMap = new ArrayList<>();
        for (int i = 0; i < cpCount - 1; i++) {
            getConstantUtf8(constantPoolMap);
        }

        Set<String> importSet = getImportStringList(constantPoolMap);
        for (String item : importSet) {
            fileSet.add(item + ".class");
        }
        fileSet.remove(classPath);//删除自己
        return fileSet;
    }

    private static void getConstantUtf8(List<String> constantPoolMap) {
        // 切分并初始化指针,根据常量的类型解析
        String tagHexString = cutString(1);
        if (tagHexString == null) {
            return;
        }
        int tag = Integer.parseInt(tagHexString, 16);
        if (tag == Constant.constant_tag_utf8) {
            String lengthHexString = cutString(Constant.utf8_length_length);
            int length = Integer.parseInt(lengthHexString, 16);
            String bytes = cutString(length * Constant.utf8_bytes_length);
            String bytesString = HexUtil.hexStr2Str(bytes);
            constantPoolMap.add(bytesString);
        } else if (tag == Constant.constant_tag_int) {
            cutString(Constant.int_bytes_length);
        } else if (tag == Constant.constant_tag_float) {
            cutString(Constant.float_bytes_length);
        } else if (tag == Constant.constant_tag_long) {
            cutString(Constant.long_bytes_length);
        } else if (tag == Constant.constant_tag_double) {
            cutString(Constant.double_bytes_length);
        } else if (tag == Constant.constant_tag_class) {
            cutString(Constant.class_index_length);
        } else if (tag == Constant.constant_tag_string) {
            cutString(Constant.string_index_length);
        } else if (tag == Constant.constant_tag_fieldRef) {
            cutString(Constant.filedRef_index_length);
            cutString(Constant.filedRef_index2_length);
        } else if (tag == Constant.constant_tag_methodRef) {
            cutString(Constant.methodRef_index_length);
            cutString(Constant.methodRef_index2_length);
        } else if (tag == Constant.constant_tag_interfaceMethodRef) {
            cutString(Constant.interfaceMethodRef_index_length);
            cutString(Constant.interfaceMethodRef_index2_length);
        } else if (tag == Constant.constant_tag_nameAndType) {
            cutString(Constant.nameAndType_index_length);
            cutString(Constant.nameAndType_index2_length);
        } else if (tag == Constant.constant_tag_methodHandle) {
            cutString(Constant.methodHandle_reference_kind_length);
            cutString(Constant.methodHandle_reference_index_length);
        } else if (tag == Constant.constant_tag_methodType) {
            cutString(Constant.methodType_descriptor_index_length);
        } else if (tag == Constant.constant_tag_invokeDynamic) {
            cutString(Constant.invokeDynamic_bootstrap_method_attr_index_length);
            cutString(Constant.invokeDynamic_name_and_type_index_length);
        }
    }

    /**
     * 获取import列表
     *
     * @return
     */
    private static Set<String> getImportStringList(List<String> constantUtf8List) {
        Set<String> set = new HashSet<String>();
        for (String info : constantUtf8List) {
            if (info.contains("/") && !info.contains("(") && !info.startsWith("/") && !info.startsWith(".")
                    && !info.contains("<") && !info.contains(";") && !info.startsWith("java/lang/")
                    && !info.endsWith(".class")) {
                set.add(info);
            }
        }
        return set;
    }

    /**
     * 切分十六进制字符串 并修改指针当前位置
     *
     * @param byteSize 切分字节长度
     * @return
     */
    private static String cutString(int byteSize) {
        int len = byteSize * 2;
        if (hexString.length() >= start_pointer + len) {
            String cutStr = hexString.substring(start_pointer, start_pointer + len);
            // 初始化指针
            start_pointer = start_pointer + len;
            return cutStr;
        }
        return null;
    }

}

package com.dengxq.jarslimming.constant;

public class Constant {

    public static final int constant_tag_utf8 = 1;
    public static final int constant_tag_int = 3;
    public static final int constant_tag_float = 4;
    public static final int constant_tag_long = 5;
    public static final int constant_tag_double = 6;
    public static final int constant_tag_class = 7;
    public static final int constant_tag_string = 8;
    public static final int constant_tag_fieldRef = 9; //类中的字段
    public static final int constant_tag_methodRef = 10; //类中的方法
    public static final int constant_tag_interfaceMethodRef = 11; //接口实现方法
    public static final int constant_tag_nameAndType = 12; //字段或方法的名称和类型
    public static final int constant_tag_methodHandle = 15; //方法句柄
    public static final int constant_tag_methodType = 16;//方法类型
    public static final int constant_tag_invokeDynamic = 18;


    public static final int utf8_length_length = 2;

    public static final int utf8_bytes_length = 1;
    public static final int int_bytes_length = 4;
    public static final int float_bytes_length = 4;
    public static final int long_bytes_length = 8;
    public static final int double_bytes_length = 8;

    public static final int class_index_length = 2;
    public static final int string_index_length = 2;

    public static final int filedRef_index_length = 2;
    public static final int filedRef_index2_length = 2;

    public static final int methodRef_index_length = 2;
    public static final int methodRef_index2_length = 2;

    public static final int interfaceMethodRef_index_length = 2;
    public static final int interfaceMethodRef_index2_length = 2;

    public static final int nameAndType_index_length = 2;
    public static final int nameAndType_index2_length = 2;

    public static final int methodHandle_reference_kind_length = 1;
    public static final int methodHandle_reference_index_length = 2;

    public static final int methodType_descriptor_index_length = 2;

    public static final int invokeDynamic_bootstrap_method_attr_index_length = 2;
    public static final int invokeDynamic_name_and_type_index_length = 2;
}

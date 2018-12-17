# jarSlimming
jar包瘦身，针对java， scala等生成的jar包，根据入口类优化，删除没有被引用的类，spark使用时效果明显


使用方法：java -jar jarSlimming.jar 参数1 参数2

参数1：被压缩包的包名 如：./jarSlimming.jar

参数2：被压缩包的入口类 如：com.dengxq.jarslimming.GetImports

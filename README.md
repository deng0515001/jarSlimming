# jarSlimming
jar包瘦身，针对java， scala等生成的jar包，根据入口类优化，删除没有被引用的类，spark使用时效果明显

remove unused class from your jar


使用方法：

第一步：下载jarSlimming-1.0.jar到本地。

第二步：java -jar jarSlimming-1.0.jar 参数1 参数2 参数3(选填)

参数1(必填)：输入jar的路径 如：./input.jar

参数2(必填)：输入jar包的入口类/入口包路径 如：com.dengxq.jarslimming.GetImports 或 com.dengxq.jarslimming

参数3(选填)：输出包路径 ./output.jar

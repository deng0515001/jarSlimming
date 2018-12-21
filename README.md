# jarSlimming
jar包瘦身，针对java， scala等生成的jar包，根据入口类优化，删除没有被引用的类，spark使用时效果明显

remove unused class from your jar


使用方法：

第一步：下载jarSlimming-1.0.jar到本地。

第二步：java -jar jarSlimming-1.0.jar 参数1 参数2 参数3(选填)

参数1(必填)：输入jar的路径 如：./input.jar

参数2(选填)：输入jar包的入口类/入口包路径 如：com.dengxq.jarslimming.GetImports 或 com.dengxq.jarslimming，
没有填写时会使用manifest的MainClass作为入口类.

参数3(选填)：输出包路径 ./output.jar

说明：
1.在输入jar和输入入口没有变化时，会直接退出，不会耗时。
2.直接使用jar包内操作，耗时短。

缺陷：
1.目前在处理各种动态加载class时，可能会存在问题，如果您遇到了，可以留言。

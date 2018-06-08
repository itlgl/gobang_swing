# gobang_swing
swing版的五子棋代码，代码重新排版了一下，详细信息看[这里](https://blog.csdn.net/lgl1170860350/article/details/23188715)

# 在IDEA工具中如何导出jar包
参考：[IntelliJ IDEA导出Java 可执行Jar包](http://blog.sina.com.cn/s/blog_3fe961ae0102uy42.html)

打开`File -> Project Structure -> Artifacts`，如下图：
![20186812231.png](https://github.com/itlgl/gobang_swing/raw/master/screenshots/20186812231.png)
需要注意的是，在`Output Layout`中需要自己点`+`按钮将要输出到jar的东西一个一个添加进去。添加过程：

1、 点下面的`Using Existing Manifest...`按钮，选择项目根目录下的`META-INF/MANIFEST.MF`文件

2、 在META-INF的设置里面添加上`Main class`

3、 在`+`按钮里面选择`Module output`将编译的class文件添加进去

4、 在`+`按钮里面选择`File`，将src目录下的三张图片添加进去

5、 完成，点击`OK`按钮保存

之后，就可以通过`Build -> Build Artifacts...`导出jar包到release目录。

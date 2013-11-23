
用法
====================
新建一个项目基本结构如下：
    --
      |--bin
      |--src_
      |      |--action_
      |                |--Test
      |      |--model
      |
      |--view_
      |       |--Test_
      |               |index.vm
      |
      |--public
      |--libs

bin是用来存储编译后的.class文件，这个目录名称可以配置为将来的热加载类路径。
src用来存储源码文件，为了安全期间所有Action单独放一个包。
view存储模版视图文件。
public用来存储静态文件资源。
libs用来放一些第三方的库，目前依赖Velocity/jetty等库（源码里面都附带了所有第三方jar）。


URL路由
=====================
  http://localhost:8080/Test/index
将会调Test类的index方法(默认就是index所以index可省略)。
Test类推荐继承Controller，也可以是Servlet(这种情况下就不提供方法路由的功能)，其他情况下会报错。
为了安全起见，这写方法必须是public才能调用。

如果方法后面有死路径则会将其作为参数传入，例如：
  http://localhost:8080/Test/index/123
将会把123作为index的方法参数传入。

模板渲染
=====================
如果在index方法中调用render()方法，将会渲染view/Test/index.vm模版视图。当然也可以显市制定模版路径。
如果URL后面有后缀名，例如：http://localhost:8080/Test/index.xml。将会渲染view/Test/index.xml模版文件，而不是index.vm

运行
=====================
可以用命令行执行JsvServer.main函数。
或者在自己的工程中的某个地方的main函数中调用JsvServer的start方法。

参数有： action包名，端口号，热加载类路径，是否调试模式。

加入拦截
=====================
Controller有before和after，可以覆盖以实现一些切面逻辑。
最标准的做法是每个项目都用继承去实现自己的Controller，然后其他类都继承这个自定义的Controller。
如果before返回true，after会确保被运行，所以可以在这两个方法中实现打开或者关闭数据库链接也可以。
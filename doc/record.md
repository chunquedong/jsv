
Record数据库接口
====================

Record模式类似与一个ORM的工具。


概念
-------------
数据库的表结构叫做Schema，数据库的字段叫做Field，数据库的一行叫做Record。

建立链接
-------------
    ConnectionPool connectionPool = new ConnectionPool(driver, url, userName, passWord, 20);
    Context.setConnection(connectionPool.open());
    Context c = new Context();
建立链接后Context负责所有数据库操作。

增删查改
--------------
首先需要定义一个Schema的表结构。
然后调用Context的方法：insert/deleteById/list/update等方法。

更多例子见/jsvRecord/test下的BaseTest.java文件。
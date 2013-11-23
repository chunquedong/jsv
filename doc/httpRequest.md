
Http请求响应过程
====================

Http是基于TCP/IP之上的协议。

基本流程
--------------------

                  请求行/请求头/请求体
         ------------------------------------->
浏览器                                            Web服务器 --> 应用代码（servlet/JSP/.html）
         <-------------------------------------
                  响应状态/响应头/响应体

请求行： GET/POST URL?querystring
请求头： Content-Length/Connection/User-Agent/Accept-Language/Referer/Cookie/Host...
请求体： form or file 只有POST请求才有

响应状态： 信息类(100-199)/响应成功(200-299)/重定向类(300-399)/客户端错误类(400-499)/服务端错误类(500-599)
响应头： Date/Content-Type/Content-Length
响应体： HTML/XML/JSON


URL
-------------------
URL由下面几部分组成： shcema://host:port/path?querystring
例如：http://www.sogou.com/test?id=123

POST/GET
-------------------
一般GET请求比较短小，会显示在浏览器地址栏。
POST用来发送大量数据，可以带一个请求体。

除了这两个还有DELETE，PUT。
标准提倡GET用于只读请求，POST用于新建请求，DELETE用于删除请求，PUT用于更新请求。
由于人懒，所有一般只用POST和GET。

Cookie
-------------------
Http协议是无状态的，为了跟踪用户状态，所以有了Cookie。
Cookie可以用来保存一些数据在客户端，一般自动登陆都是用Cookie实现的，服务器端Session也是基于Cookie实现的。
浏览器请求服务器的时候会把所有Cookie中和当前域名匹配的Cookie都发送到服务器。
Cookie是服务器响应中要求添加的，一般有一个过期时间的限制和匹配域名。

Content-Type
-------------------
Content-Type用来表示返回内容的格式类型。
例如：Content-Type : text/html;charset=ISO-8859-1


Status-Code
-------------------
响应的状态码，200表示OK，400是文件未找到。浏览器受到300重定向后，会转到制定的URL。


Java Servelt
====================
Java 把请求封装为Request对象，把响应封装为Respose对象。
Servelt的就是读Request对象，然后将结果写入Respose对象。
所以只需要Request和Respose对象就可以实现所有服务器端逻辑。


MVC
====================
主要意思是模型/视图/控制分离。
所以不要直接访问视图，先访问控制器，再forward过去。


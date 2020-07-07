"# spring-demo-first" 

--spring
1.容器启动读取配置文件，扫描指定包下的类
2.将指定包下的类解析成beanDefintion
3.将beanDefintion注册到IOC容器中,用beanDefintionMap保存
4.然后遍历map,通过beanDefintion创建对象instance，并保存到map中
5.然后对instance进行依赖注入，判断instance是否存在注解


--springmvc
我们知道在DispatcherServlet中，发起请求后，会先通过url到handlerMapping中获取handler，然后再拿着这个handler去所有的handlerAdapter中通过isSupport方法找到属于自己的adapter，那后通过这个apdater指定调用handler中的那个方法来处理请求。

https://blog.csdn.net/liuhaibo_ljf/article/details/106000158
HandlerAdapter的实现类：                                                                                               
		1.SimpleControllerHandlerAdapter
		2.SimpleServletHandlerAdapter
		3.HttpRequestHandlerAdapter
		4.AnnotationMethodHandlerAdapter
		5.RequestMappingHandlerAdapter
		
--AOP
在容器初始化的时候，AOP就已经完成了切面代理。
1.通过AdvisedSupport中的pointCutMatch()方法来判断是否生成AOP代理对象
2.AdvisedSupport在设置targetClass的时候，就已经根据配置，生成好了当前类所有的拦截器链，并存储到map中
3.创建AOP代理对象时，传入AdvisedSupport,AdvisedSupport中存储了原生类的class和对象
4.代理对象的invoke()方法,则是根据AdvisedSupport获取到拦截器链，然后封装成WQMethodInvocation
5.WQMethodInvocation的proceed()方法用于执行拦截器链
6.不同的拦截器对应不同的advised。拦截器通过反射执行对应的切面逻辑。
7.拦截器执行完后，直接执行原生方法。











https://blog.csdn.net/kuailebuzhidao/article/details/88355236?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.edu_weight&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.edu_weight

MyBatis的主要的核心部件有以下几个：

Configuration：MyBatis所有的配置信息都维持在Configuration对象之中，核心类；
SqlSession：作为MyBatis工作的主要顶层API，表示和数据库交互的会话，完成必要数据库增删改查功能；
Executor：MyBatis执行器，是MyBatis 调度的核心，负责SQL语句的生成和查询缓存的维护；
StatementHandler：封装了JDBC Statement操作，负责对JDBC statement 的操作，如设置参数、将Statement结果集转换成List集合。
ParameterHandler：负责对用户传递的参数转换成JDBC Statement 所需要的参数；
ResultSetHandler：负责将JDBC返回的ResultSet结果集对象转换成List类型的集合；
MappedStatement：MappedStatement维护了一条<select|update|delete|insert>节点的封装；
MapperProxy和MapperProxyFactory：Mapper代理，使用原生的Proxy执行mapper里的方法。

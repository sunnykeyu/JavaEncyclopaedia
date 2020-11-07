# 初识JVM
## 一 自动内存管理
### 1.1 JVM内存结构

![](C:\Users\sunkeyu\Desktop\java虚拟机运行时数据区.png)

- #### 程序计数器(Program Counter Register)

  作用：记住下一条JVM指令的执行地址

  特点：

  - 线程私有
  - 不会存在OutOfMemoryError（内存溢出）

- #### 虚拟机栈（Java Virtual Machine Stacks）

  定义：

  - 每个线程运行是所需要的内存，程序虚拟机栈

  - 每个栈由多个栈帧（Stack Frame）组成，对应着每次方法调用时所占用的内存

  - 每个线程只能有一个活动栈帧，对应着当前正在执行的那个方法

  问题：

  1. 垃圾回收是否涉及栈内存？

     答：不需要，栈内存是由方法调用产生的栈帧内存，栈帧内存随着每一个方法调用结束而自动释放。

  2. 栈内存分配越大越好吗？（-Xss指定栈内存大小）

     答：栈内存越大，线程数越少，栈内存大小只会提升方法调用的个数（递归深度）。

  3. 方法内的局部变量是否线程安全？

     答：如果方法内局部变量没有逃离方法的作用范围，那么它是线程安全的，如果局部变量引用了对象（不是基本类型），并逃离了方法的作用范围，那么他就是线程不安全的。

  栈内存溢出：

  - 栈帧过多导致栈内存溢出（递归调用深度过深）StackOverFlowError
  - 虚拟机栈动态扩展时无法申请到足够的内存时 OutOfMemoryError

  线程运行诊断：

  1. top  检测cpu占用过多的进程pid
  2. ps H -eo pid,tid,%cpu | grep [pid]  找到cpu占用过高的线程
  3. jstack [pid]  查看java虚拟机栈的线程
  4. 将第二步找到的tid转换为16进制，并找到对应的线程

- #### 本地方法栈（Native Method Stacks）

  native方法运行需要的内存空间

  表现形式类似于虚拟机栈

- #### 堆（Java Heap）

  定义：虚拟机启动时创建，用来存放类实例和数组

  特点：

  - 线程共享，堆中对象都需要考虑线程安全问题
  - 有垃圾回收机制
  - 物理上不连续
  
  堆内存诊断：
  
  - jps工具：查看当前系统中有哪些Java进程
  
  - jmap工具：查看堆内存占用情况
  
    jmap -heap [pid]
  
  - jconsole工具：图形界面的，多功能监测工具，可以连续监测
  - jvisualvm工具：堆转储 dump

- #### 方法区（Method Area）

  - JDK1.8以前会导致永久代内存溢出，java.lang.OutOfMemoryError: PermGen space（-XX:MaxPermSize=8m）
  - JDK1.8后会导致元空间（Metaspace 本地内存中）内存溢出 java.lang.OutOfMemoryError: Metaspace (-XX:MaxMetaspaceSize=8m 将元空间大小设置为8m)

  场景:

  sping、mybatis等框架通过cglib运行期间生成类

  ##### 运行时常量池

  javap -v [class]  反编译class字节码

  StringTable：

  - 常量池中的字符串仅是符号，第一次用到时才会变为对象

  - 利用串池的机制，来避免重复创建字符串对象

  - 字符串变量拼接的原理是StringBuilder（jdk1.8）

  - 字符串常量拼接的原理是编译期优化

  - 可以使用intern方法，主动将串池中还没有的字符串对象放入串池

    1.8 将这个字符串对象尝试放入串池，如果有则不会放入，如果没有则放入串池，会把串池中的对象返回

    1.6将这个字符串对象尝试放入串池，如果有则不会放入，如果没有会把此对象复制一份，放入串池，并把串池对象返回

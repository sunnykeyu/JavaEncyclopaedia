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
  - 虚拟机栈动态扩展时无法申请到足够的内存时 OutOfMemoryError（建立过多线程导致内存溢出，可通过减少最大堆和减少栈容量来换取更多的线程）

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

  定义：方法区的一部分，类加载后存放编译期生成的各种字面量和符号引用

  

  javap -v [class]  反编译class字节码

  StringTable：

  - 常量池中的字符串仅是符号，第一次用到时才会变为对象

  - 利用串池的机制，来避免重复创建字符串对象

  - 字符串变量拼接的原理是StringBuilder（jdk1.8）

  - 字符串常量拼接的原理是编译期优化

  - 可以使用intern方法，主动将串池中还没有的字符串对象放入串池
  
    1.8 将这个字符串对象尝试放入串池，如果有则不会放入，如果没有则放入串池，会把串池中的对象返回
  
    1.6将这个字符串对象尝试放入串池，如果有则不会放入，如果没有会把此对象复制一份，放入串池，并把串池对象返回

### 1.2 垃圾收集器（GC）与内存分配策略
- #### 判断对象是否应该被回收

  ###### 引用计数算法(其他语言)

  定义：给对象添加一个引用计数器，每当有一个地方引用它时，计数器值加1；当引用失效时，计数器值减1；任何时刻计数器都为0的对象就是不会再被使用的。

  缺陷：很难解决对象之间相互循环引用的问题。（A对象实例与B对象实例相互引用，但都不被其他对象所应用时）

  ###### 根搜索算法（Java、C#）

  定义：通过一系列“GC Roots”对象作为起始点，从这些节点向下搜索，搜索走过的路径称为引用链（Reference Chain），当一个对象GC Roots 没有任何引用链相连（GC Roots 到对象不可达），那么此对象就是不会再被使用的。

  GC Roots对象：

  - 虚拟机栈（栈帧中的本地变量表）中引用的对象。
  - 方法区中的类静态属性引用的对象。
  - 方法区中的常量引用对象。
  - 本地方法栈中JNI（native方法）的引用对象。

  ###### 两次标记

  根搜索算法不可达对象不会立即执行垃圾回收，需要经历两次标记：

  - 第一次标记：通过GC roots遍历，找到不在关系网内的对象。并检查是否需要执行finalize()方法。（也就是说如果没重写finalize()则只需要标记一次，然后就可以GC）
  - 第二次标记：如果对象实现了Object的finalize()方法，且该对象finalize()方法没有被调用过(finalize()方法只会被自动调用一次)，则会判定这个对象有必要执行finalize()方法并放入F-Queue队列中。虚拟机会自动建立一个低优先级的Finalizer线程去执行队列中对象的finalize()方法。对队列中的对象再遍历一次，对还是不在关系网内的对象进行标记。

  使用：

  finalize()能做的所有工作，try-finally或其他方式都可以做得更好更及时，所以一般不要自己调用finalize()。

  ###### 引用

  - 强引用：Object obj = new Object();  只要强引用还存在，垃圾收集器永远不会回收掉被引用的对象。
  - 软引用：还有用，但并非必需的对象。系统将要发生OOM之前，会把这些对象列入回收范围并进行第二次回收。如果这次回收后还是没有足够的内存，才会抛出OOM异常。SoftReference类来实现软引用。
  - 弱引用：非必需对象，且强度比软引用更弱，被弱引用关联的对象只能生存到下一次垃圾收集发生之前。WeakReference类来实现软引用。
  - 虚引用（幽灵引用、幻影引用）：最弱的一种引用关系。不会对生存时间构成影响，也无法通过虚引用来取得一个对象实例。设置虚引用后，在这个对象被GC回收时会收到一个系统通知。PhantomReference类实现虚引用。

  ###### 方法区的垃圾回收

  - 废弃常量

    没有其他地方引用这个常量

  - 无用的类

    该类所有的实例都已经被回收

    加载该类的ClassLoader已经被回收

    该类对应的java.lang.Class对象没有在任何地方被引用，无法通过反射访问该类的方法

- #### 垃圾收集算法

  ###### 标记-清除算法

  **特点**：

  首先标记出所有需要回收的对象，在标记完成后统一回收掉所有被标记的对象。

  **缺点**：

  - 效率问题，标记和清除过程中的效率都不高
  - 空间问题，标记清除后会产生大量的不连续的内存碎片，导致程序在以后运行过程中需要分配较大对象是无法找到足够的连续内存而不得不提前触发另一次垃圾回收。

  ######  复制算法

  **特点：**

  将可用内存按容量划分为大小相等的两块，每次只使用其中一块，当其中一块内存用完了，就将还存活着的对象复制到另一块上，再把已使用过的内存空间一次清理掉。

  **优点：**

  内存分配时不用考虑内存碎片等复杂情况，只需要移动堆定指针，按顺序分配内存。

  **应用**：

  回收新生代，将内存分为一块较大的Eden空间和两块较小的Survivor空间，每次使用Eden和其中的一块Survivor，当回收时，将Eden和Survivor中还存活着的对象一次性得地拷贝到另一块Survivor空间上，最后清理掉Eden和刚才使用过的Survivor空间。

  HotSpot：默认Eden和Survivor大小比例为8:1:1(可用内存空间为新生代90%)

  ###### 标记-整理算法

  **特点**：

  让所有存活的对象都向一端移动，然后直接清理掉端边界以外的内存。

  ###### 分代收集算法

  **特点**：

  根据对象的存活周期的不同将内存划分为几块（新生代和老年代），这样可以根据各代特点而采用最合适的手机算法。

  新生代：复制算法

  老年代：标记-清理算法 或 标记-整理算法

- #### 垃圾收集器

  内存回收的具体实现。

  ###### HotSpot

  ![image-20201109195606074](C:\Users\sunkeyu\AppData\Roaming\Typora\typora-user-images\image-20201109195606074.png)

  如果两个收集器之间存在连线，说明他们可以搭配使用。

  ###### Serial 收集器

  缺点：垃圾回收时用户正常工作的线程会全部停掉。

  优点：简单高效，没有线程交互的开销，桌面程序虚拟机管理的内存不大，停顿时间可以控制在几十毫秒到一百多毫秒。

  ParNew收集器

  Serial收集器的多线程版本，Server模式下的虚拟机首选新生代收集器，单线程环境下表现效果不如Serial。

  ###### Parallel Scavenge收集器

  使用复制算法的并行多线程收集器，保证垃圾回收吞吐量可控制。

  停顿时间越短就越适合与用户交互的重新，良好的响应速度能提升用户的体验；高吞吐量可以最高效率的利用CPU时间，尽快的完成程序运算任务，适合在后台运算不需要太多交互的任务。

  通过参数控制最大停顿时间（-XX:MaxGCPauseMillis）和吞吐量大小(-XX:GCTimeRatio)

  ###### Serial Old 收集器

  Serial的老年代版本，使用标记-整理算法

  ###### Parallel Old收集器

  Parallel Scavenge老年代版本使用标记-整理算法

  ###### CMS收集器

  获取最短回收停顿时间为目的的收集器，重视服务的响应速度，基于标记-清除算法，应用于B/S系统服务端。

  步骤：

  1. 初始标记
  2. 并发标记
  3. 重新标记
  4. 并发清理

  初始标记、重新标记仍然需要“Stop The World”, 初始标记仅仅标记GC Roots能直接关联到的对象，速度很快，并发标记阶段进行GC Roots Tracing，重新标记阶段是为了修正并发标记期间，因用户程序继续运作而导致标记产生变动的那一部分对象的标记记录，时间比初始标记长，但远比并发标记时间短。

  优点：并发收集、低停顿

  缺点：

  - CMS收集器对CPU资源非常敏感，当CPU不足四个时，对用户程序影响很大
  - CMS无法处理浮动垃圾（并发清理时程序运行产生的新的垃圾），老年代空间需要预留一部分提供给并发收集时程序运作使用。如果预留的内存无法满足程序需要，就会出现“Concurrent Mode Failure”，此时需要临时启用Serial Old收集器
  - 收集结束时会产生大量空间碎片

  ###### G1收集器

- #### 内存分配与回收策略

  给对象分配内存以及回收分配给对象的内存

  - 大多数情况下，对象在牺牲带Eden区中分配，当Eden区没有足够的空间进行分配时，虚拟机将发起一次Minor GC。
  - 大对象（需要大量连续内存空间的java对象）直接进入老年代（Serial、ParNew收集-XX:PretenureSizeThreshold设置）
  - 长期存活的对象将进入老年代，虚拟机给每个对象定义了一个对象年龄计数器。MinGC后进入Survivor空间则将年龄设为1，并在Survivor空间达到-XX:MaxTenuringThreshold设置的年龄（默认15）后进入老年代
  - 如果Survivor空间中相同的年龄所有对象大小的总和大于Survivor空间的一半，年龄大于或等于该年龄的对象就可以直接进入老年代。	


- #### JDK命令行工具

  ###### jps：虚拟机进程状况工具

  jps [options] [hostid]   主要选项-q -m -l -v

  ###### jstat：虚拟机统计信息监视工具

  显示本地货远程虚拟机进程中的类装载、内存、垃圾收集、JIT编译等运行数据，运行期定位虚拟机性能问题的首选工具。

  jstat [ option vmid [interval [s|ms] [count]] ]

  ###### jinfo：Java配置信息工具

  jinfo [option] pid

  ###### jmap：java内存映像工具

  用于生成堆转储快照（称为heapdump或dump）及查询finalize执行队列、堆详细信息

  ###### jhat：虚拟机堆转储快照分析工具

  ###### jstack：java堆栈跟踪工具

  ###### jconsole：java监视与管理控制台

  ###### VisualVm：多合一故障处理工具

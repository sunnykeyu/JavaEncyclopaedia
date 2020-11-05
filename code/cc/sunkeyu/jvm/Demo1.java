package cc.sunkeyu.jvm;

/**
 * 演示栈内存溢出
 * -Xss256k java.lang.StackOverflowError 默认Xss1m
 */
public class Demo1 {
    private static int count = 0;
    public static void main(String[] args) {
        try {
            method();
        }catch (Throwable e){
            e.printStackTrace();
            System.out.println(count);
        }
    }

    private static void method(){
        count++;
        method();
    }
}

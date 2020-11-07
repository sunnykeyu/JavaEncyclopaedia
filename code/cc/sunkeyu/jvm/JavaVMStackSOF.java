package cc.sunkeyu.jvm;

/**
 * <h3>JavaEncyclopaedia</h3>
 * <p></p>
 *
 * @author : sunkeyu
 * @date : 2020-11-07 15:21
 **/
public class JavaVMStackSOF {
    private int count = 0;

    private void stackLeak(){
        count++;
        stackLeak();
    }

    public static void main(String[] args) {
        JavaVMStackSOF javaVMStackSOF = new JavaVMStackSOF();
        try{
            javaVMStackSOF.stackLeak();
        }catch (Throwable throwable){
            System.out.println(javaVMStackSOF.count);
            throwable.printStackTrace();
        }
    }
}

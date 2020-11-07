package cc.sunkeyu.jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * <h3>JavaEncyclopaedia</h3>
 * <p></p>
 *
 * @author : sunkeyu
 * @date : 2020-11-07 15:34
 **/
public class RuntimeConstantPoolOOM {
    public static void main(String[] args) {
        List<String> strings = new ArrayList<String>();
        int i = 0;
        while(true){
            strings.add(String.valueOf(i++).intern());
            System.out.println(i);
        }
    }
}

package cc.sunkeyu.jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * <h3>JavaEncyclopaedia</h3>
 * <p></p>
 *
 * @author : sunkeyu
 * @date : 2020-11-07 14:11
 **/
public class Demo3 {
    public static void main(String[] args) {
        List<String> strings = new ArrayList<String>();
        for(int i = 0; i < 1000000; i++){
            String a = String.valueOf(i % 10);
            strings.add(a);
        }
        while(true){
            System.out.println(strings.size());
        }
    }

}

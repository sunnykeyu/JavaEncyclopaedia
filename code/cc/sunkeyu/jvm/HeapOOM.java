package cc.sunkeyu.jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * <h3>JavaEncyclopaedia</h3>
 * <p></p>
 *
 * @author : sunkeyu
 * @date : 2020-11-07 14:42
 **/
public class HeapOOM {
    static class OOMObject{

    }

    public static void main(String[] args) {
        List<OOMObject> list = new ArrayList<OOMObject>();
        while (true){
            list.add(new OOMObject());
        }
    }

}

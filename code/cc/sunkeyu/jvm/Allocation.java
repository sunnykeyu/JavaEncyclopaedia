package cc.sunkeyu.jvm;

/**
 * <h3>JavaEncyclopaedia</h3>
 * <p></p>
 *
 * @author : sunkeyu
 * @date : 2020-11-10 10:04
 **/
public class Allocation {
    /**
     * -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:SurvivorRatio=8 -XX:+PrintGCDetails
     */
    private static final int _1MB = 1024 * 1024;

    public static void testAllocation() {
        byte[] l1, l2, l3, l4;
        l1 = new byte[2 * _1MB];
        l2 = new byte[2 * _1MB];
        l3 = new byte[2 * _1MB];
        l4 = new byte[4 * _1MB];
    }

    /**
     * -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:SurvivorRatio=8 -XX:+PrintGCDetails -XX:PretenureSizeThreshold=3145728
     */
    public static void testPretenureSizeThreshold() {
        byte[] allocation;
        allocation = new byte[4 * _1MB];
    }

    /**
     * -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:SurvivorRatio=8 -XX:+PrintGCDetails -XX:MaxTenuringThreshold=1 -XX:+PrintTenuringDistribution -XX:+UseSerialGC
     */
    public static void testTenuringTreshold() {
        byte[] l1, l2, l3;
        l1 = new byte[_1MB / 4];
        l2 = new byte[4 * _1MB];
        l3 = new byte[4 * _1MB];
        l3 = null;
        l3 = new byte[4 * _1MB];
    }

    public static void main(String[] args) {
        testTenuringTreshold();
    }
}

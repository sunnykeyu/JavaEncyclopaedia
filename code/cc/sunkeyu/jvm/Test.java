package cc.sunkeyu.jvm;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * <h3>JavaEncyclopaedia</h3>
 * <p></p>
 *
 * @author : sunkeyu
 * @date : 2020-11-07 15:56
 **/
public class Test {
    public static void main(String[] args) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong("1604734964230")), ZoneId.systemDefault());
        int month = localDateTime.getMonthValue();
        int day = localDateTime.getDayOfMonth();
        int hour = localDateTime.getHour();
        System.out.println(month + "  " + day + "  " + hour);
    }
}

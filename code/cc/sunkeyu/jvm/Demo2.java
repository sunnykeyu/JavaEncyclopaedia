package cc.sunkeyu.jvm;

import java.util.Arrays;
import java.util.List;

/**
 * 演示递归调用栈内存溢出
 * -Xss256k java.lang.StackOverflowError 默认Xss1m
 */
public class Demo2 {
    public static void main(String[] args) {
        Dept dept = new Dept() {{
            this.setName("dev");
        }};
        Emp emp1 = new Emp();
        emp1.setName("sun");
        emp1.setDept(dept);
        Emp emp2 = new Emp();
        emp2.setName("jiang");
        emp2.setDept(dept);
        dept.setEmps(Arrays.asList(emp1, emp2));
        String json = JSONObject
    }
}

class Emp {
    private String name;
    private Dept dept;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Dept getDept() {
        return dept;
    }

    public void setDept(Dept dept) {
        this.dept = dept;
    }
}

class Dept {
    private String name;
    private List<Emp> emps;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Emp> getEmps() {
        return emps;
    }

    public void setEmps(List<Emp> emps) {
        this.emps = emps;
    }
}
package test;

import java.lang.reflect.Field;

/**
 * @Author：Eric
 * @Date：2020/4/4 22:44
 */
public class TestRef {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        String str = "hello";
        //先创建一个Field对象，value这个名字就和String类中包含的char value[]是一致的
        Field field = String.class.getDeclaredField("value");
        //把访问权限设为可以访问
        field.setAccessible(true);
        //根据field中包含的信息，把str中的value这个数组获取到
        //value就是str内部存储“hello”这个内容的数组
        char[] value = (char[])field.get(str);
    }
}

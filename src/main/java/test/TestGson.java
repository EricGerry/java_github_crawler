package test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

/**
 * @Author：Eric
 * @Date：2020/4/4 22:31
 */
public class TestGson {
    static class Test{
        private int aaa;
        private int bbb;
    }
    public static void main(String[] args) {
        //1.先创建一个Gson对象
        Gson gson = new GsonBuilder().create();

        //2.把键值对数据转成json格式的字符串
/*        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("name", "曹操");
        hashMap.put("skill1", "三段跳");
        hashMap.put("skill2", "剑气");
        hashMap.put("skill3", "加攻击并回血");
        hashMap.put("skill4", "每次释放技能都加攻速");
        String result = gson.toJson(hashMap);
        System.out.println(result);*/
// 3.把JSON格式字符串转成键值对数据
        String jsonString = "{\"aaa\": 1, \"bbb\": 2}";
        //Test.class取出当前这个类的类对象
        //fromJson方法实现，依赖反射机制
        Test t = gson.fromJson(jsonString, Test.class);
        System.out.println(t.aaa);
        System.out.println(t.bbb);


    }
}

package com.demo;

import com.demo.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = com.demo.DemoApplication.class)
public class SensitiveTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter() {
        String text = "这里可以__赌__博，可以嫖娼，可以吸毒，可以开票，哈哈哈";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }

    public static void main(String[] args) {
        int n = 10;
        double avg = testdd(0);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                avg = (avg + testdd(0)) / 2;
            }
        }
        System.out.println(avg);
    }

    public static double testdd(int ns) {
        int n = 1000000, nn = 1000000;
        double avg = 0.0;
//        while (nn-- > 0) {
            int g = 0;
            while (n-- > 0) {
                int[] ii = getTenGeneral();
                for (int i = 0; i < 10; i++) {
                    if (ii[i] == 3) {
                        g += 1;
                    }
                }
            }avg = (g / 1000000.0) / 10.0;
//            if (avg!=0.0) {
//                avg = (g / 1000000.0) / 10.0;
//            } else {
//                avg = (avg + (g / 1000000.0) / 10.0) / 2;
//            }
//        }
        return avg;
    }

    int getValue(int[] nums, int n) {
        int e = n%2 == 0 ? n/2+1 : (n+1)/2;
        // 定义一个Map数组
        Map<Integer, Integer> map = new HashMap<>();
        for(int i = 0; i < n; i++){
            if(!map.containsKey(nums[i])){
                map.put(nums[i], 1);
            }else{
                map.put(nums[i], map.get(nums[i]) + 1);
                if(map.get(nums[i]) >= e)
                    return nums[i];
            }
        }
        return 0;
    }

    // 返回数组中1,2,3,4代表品质参照上表
    static int[] getTenGeneral(){
        int[] lotterys = new int[10];
        int[] q = new int[2];
        double g1 = 0.495, g2 = 0.895, g3 = 0.995, g4 = 1.0;
        double gd = Math.pow(0.895, 10);
        int guarantees = 0;
        for(int i = 0; i < 10; i++) {
            double probability = Math.random();
//            // 概率平衡
//            if (guarantees == 1) {
//                g2 += 0.01;
//            }
            // 保底
            if (i == 9 && guarantees == 0) {
                g1 = 0.0;
                g2 = 0.0;
            }
            if (probability < g1) {
                lotterys[i] = 1;
                q[0] = i+1;
            } else if (probability < g2) {
                lotterys[i] = 2;
                q[1] = i+1;
            } else if (probability < g3) {
                lotterys[i] = 3;
                guarantees = 1;
            } else if (probability < g4) {
                lotterys[i] = 4;
                guarantees = 1;
            }
        }
        return lotterys;
    }


}

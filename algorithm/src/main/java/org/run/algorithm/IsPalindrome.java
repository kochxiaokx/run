package org.run.algorithm;

/**
 * 判断一个数是不是回文数
 * 如输入121
 *   输出121
 * ----------------》true
 * 输入10
 * 输出1
 * -----------------》false
 *
 * */
public class IsPalindrome {

    public boolean solution(int num){
        if(num < 0){
            return false;
         }
        int tmp = num;
        int result = 0;
         while (num > 0){
            int last = num % 10;
            result = (result * 10) + last;
            num = num / 10;
         }
        return tmp == result;
    }

    public static void main(String[] args) {
        System.out.println(1  / 10);
    }

}

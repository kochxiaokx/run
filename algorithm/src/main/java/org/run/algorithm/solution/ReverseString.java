package org.run.algorithm.solution;

import java.util.Arrays;

/**
 * 编写一个函数，其作用是将输入的字符串反转过来。输入字符串以字符数组 char[] 的形式给出。
 *
 * 不要给另外的数组分配额外的空间，你必须原地修改输入数组、使用 O(1) 的额外空间解决这一问题。
 *
 * 你可以假设数组中的所有字符都是 ASCII 码表中的可打印字符。
 *
 * ex:1
 * 输入：["h","e","l","l","o"]
 * 输出：["o","l","l","e","h"]
 *
 * ex:2
 * 输入：["H","a","n","n","a","h"]
 * 输出：["h","a","n","n","a","H"]
 */
public class ReverseString {

    public static void main(String[] args) {
        char[] s = new char[]{'o','l','l','e','h'};
        System.out.println(Arrays.toString(s));
        reverseString(s);
        System.out.println(Arrays.toString(s));
    }

    public static void reverseString(char[] s){
        if(s == null || s.length <= 0){
            return;
        }
        int pre = 0;
        int last = s.length - 1;
        while (pre < last){
            char tmp = s[pre];
            s[pre] = s[last];
            s[last] = tmp;
            pre++;
            last --;
        }


    }


}

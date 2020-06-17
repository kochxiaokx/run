package org.run.client.algorithm;

import org.junit.Test;
import org.run.algorithm.solution.IsPalindrome;
import org.run.algorithm.solution.TwoSum;

import java.util.Arrays;

public class AlgorithmClient {
    @Test
    public void testTwoSum(){
        Integer target = 9;
        int[] array =  new int[]{1,2,3,4,5,6,7,8};
        Integer[] twoSum = TwoSum.getTwoSum(target, array);
        System.out.println(Arrays.toString(twoSum));
    }
    @Test
    public void testIsPalindrome(){
        IsPalindrome is = new IsPalindrome();
        boolean solution = is.solution(101);
        System.out.println(solution);
    }

    public static void main(String[] args) {
        Integer target = 9;
        int[] array =  new int[]{1,2,3,4,5,6,7,8};
        Integer[] twoSum = TwoSum.getTwoSum(target, array);
        System.out.println(Arrays.toString(twoSum));
    }
}

package org.run.client.algorithm;

import org.junit.jupiter.api.Test;
import org.run.algorithm.TwoSum;

import java.util.Arrays;

public class AlgorithmClient {
    @Test
    public void testTwoSum(){
        Integer target = 9;
        int[] array =  new int[]{1,2,3,4,5,6,7,8};
        Integer[] twoSum = TwoSum.getTwoSum(target, array);
        System.out.println(Arrays.toString(twoSum));
    }

    public static void main(String[] args) {
        Integer target = 9;
        int[] array =  new int[]{1,2,3,4,5,6,7,8};
        Integer[] twoSum = TwoSum.getTwoSum(target, array);
        System.out.println(Arrays.toString(twoSum));
    }
}

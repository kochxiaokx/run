package org.run.algorithm.solution;

import java.util.Arrays;

/**
 * 给定一个数组 nums，编写一个函数将所有 0 移动到数组的末尾，同时保持非零元素的相对顺序。
 * 1.必须在原数组上操作，不能拷贝额外的数组。
 * 2.尽量减少操作次数。
 */
public class MoveZeroes {


    public static void main(String[] args) {
        int[] array = new int[]{1,3,0,12,0};
        System.out.println("array:"+ Arrays.toString(array));
        moveZeroes(array);
        System.out.println("array:"+ Arrays.toString(array));
    }


    public static void moveZeroes(int[] nums){
        if(nums == null || nums.length <= 0){
            return;
        }
        int j = 0;
        for (int i = 0; i< nums.length; i++) {
            if (nums[i] != 0) {
                if(i != j) {
                    nums[j] = nums[i];
                    nums[i] = 0;
                }
                j++;
            }
        }
    }




}

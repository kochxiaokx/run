package org.run.algorithm;

import org.run.base.exception.RRException;

import java.util.HashMap;
import java.util.Map;

/**
 * 两数之和
 */
public class TwoSum {


    public static Integer[] getTwoSum(int target,int[] array){
        Map<Integer,Integer> map = new HashMap<>();
        for(int i = 0; i < array.length ; i++){
            Integer complete  = target - array[i];
            if(map.containsKey(complete)){
                return new Integer[]{map.get(complete),i};
            }else{
                map.put(array[i],i);
            }
        }
        throw new RRException("not suitable two sum");
    }
}

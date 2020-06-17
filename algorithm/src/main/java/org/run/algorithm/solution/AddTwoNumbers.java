package org.run.algorithm.solution;

import org.junit.Test;
import org.run.algorithm.Algorithem;
import org.run.algorithm.util.ListNode;

/**
 * 给出两个 非空 的链表用来表示两个非负的整数。其中，它们各自的位数是按照 逆序 的方式存储的，并且它们的每个节点只能存储 一位 数字。
 *
 * 如果，我们将这两个数相加起来，则会返回一个新的链表来表示它们的和。
 *
 * 您可以假设除了数字 0 之外，这两个数都不会以 0 开头。
 *
 * 示例：
 *
 * 输入：(2 -> 4 -> 3) + (5 -> 6 -> 4)
 * 输出：7 -> 0 -> 8
 * 原因：342 + 465 = 807
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/add-two-numbers
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class AddTwoNumbers extends Algorithem<ListNode> {


    @Override
    protected ListNode solution() {

        return null;
    }


    private ListNode solution(ListNode l1,ListNode l2){
        ListNode result = new ListNode(0);
        //或得引用
        ListNode current  = result, p = l1,q = l2;
        int carry = 0;
        while (p != null || q != null){
            int x = (p == null) ? 0 : p.getVal();
            int y = (q == null) ? 0 : q.getVal();
            int sum = x + y + carry;
            carry = sum / 10;
            //获得result的next的引用地址
            current.next= new ListNode(sum % 10);
            current = current.next; //将result的next的引用地址更新成为当前的引用
            if(p != null) p = p.next;
            if(q != null) q = q.next;

        }
        if(carry > 0){
            current.next = new ListNode(carry);
        }
        return result.next;
    }
    @Test
    public void test(){
        ListNode l1 = new ListNode(1);
        ListNode l11 = new ListNode(2);
        ListNode l12 = new ListNode(3);
        l11.setNext(l12);
        l1.setNext(l11);
        ListNode l2 = new ListNode(3);
        ListNode l21 = new ListNode(3);
        ListNode l23 = new ListNode(3);
        l21.setNext(l23);
        l2.setNext(l21);
        ListNode solution = solution(l1, l2);
        System.out.println(solution);
    }

}

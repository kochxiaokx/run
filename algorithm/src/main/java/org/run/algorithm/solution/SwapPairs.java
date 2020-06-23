package org.run.algorithm.solution;

import org.run.algorithm.util.ListNode;

/**
 * 给定一个链表，两两交换其中相邻的节点，并返回交换后的链表。
 * <p>
 * 你不能只是单纯的改变节点内部的值，而是需要实际的进行节点交换。
 * <p>
 * <p>
 * ex:
 * 给定 1->2->3->4, 你应该返回 2->1->4->3.
 */
public class SwapPairs {

    public static void main(String[] args) {
        ListNode l1 = new ListNode(1);
        ListNode l11 = new ListNode(2);
        ListNode l12 = new ListNode(3);
        ListNode l13 = new ListNode(4);
        l12.setNext(l13);
        l11.setNext(l12);
        l1.setNext(l11);
        System.out.println(l1);
        ListNode head = swapPairs2(l1);
        System.out.println(head);
    }

    /**
     * 这个解法只是单纯的交换值,并没有真正的交换节点
     * @param head
     * @return
     */
    public static ListNode swapPairs(ListNode head) {
        if(head == null){
            return null;
        }
        ListNode pre = head;
        ListNode next = head.next;
        while (next != null) {
            int tmp = pre.val;
            pre.val = next.val;
            next.val = tmp;
            pre = next.next;
            if (pre != null) {
                next = pre.next;
            } else {
                next = null;
            }
        }
        return head;
    }

    /**
     * 交换节点
     * @param head
     * @return
     */
    public static ListNode swapPairs2(ListNode head) {
        if(head == null){
            return null;
        }


        return head;
    }



}

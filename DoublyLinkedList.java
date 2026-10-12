public class DoublyLinkedList {
    Node head;
    Node tail;

    DoublyLinkedList() {
        this.head = null;
        this.tail = null;
    }
    public void insertAtFront(Node node) {
        if (head == null) {
            head = node;
            tail = node;
            node.next = null;
            node.prev = null;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
            head.prev = null;
        }
    }
    public void removeNode(Node node) {
        if (node == null) {
            return;
        }

        if (node == head) {
            head = head.next;

            if (head != null) {
                head.prev = null;
            }
        }

        if (node == tail) {
            tail = tail.prev;

            if (tail != null) {
                tail.next = null;
            }
        }

        if (node.prev != null) {
            node.prev.next = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        }

        node.next = null;
        node.prev = null;
    }
    public void moveToFront(Node node) {
        removeNode(node);
        insertAtFront(node);
    }
    public Node RemoveLeastRecentlyUsed() {
        if (tail == null) {
            return null;
        }

        Node removedNode = tail;

        removeNode(tail);

        return removedNode;
    }
}
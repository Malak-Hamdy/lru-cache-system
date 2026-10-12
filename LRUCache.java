public class LRUCache {
    int capacity;
    int currentSize;
    private HashTable table;
    private DoublyLinkedList list;

    public LRUCache(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("capacity must be at least 1");
        }
        this.capacity = capacity;
        this.currentSize = 0;

        list = new DoublyLinkedList();
        // Table is larger than capacity, so it always has a free slot.
        table = new HashTable(2 * capacity + 1);
    }

    public int get(int key) {

        Node node = table.search(key);

        if (node == null) {
            return -1;
        }

        list.moveToFront(node);

        return node.data;
    }
    public void put(int key, int value) {

        Node existingNode = table.search(key);

        if (existingNode != null) {
            // Key exists
            existingNode.data = value;
            list.moveToFront(existingNode);

        } else {
            // New key
            if (currentSize >= capacity) {
                // Cache is full: evict the least recently used entry
                Node evicted = list.RemoveLeastRecentlyUsed();
                table.delete(evicted.key);
                currentSize--;
            }

            Node newNode = new Node(key, value);
            list.insertAtFront(newNode);
            table.insert(key, newNode);
            currentSize++;
        }
    }

    public boolean remove(int key) {

        Node node = table.search(key);

        if (node == null) {
            return false;
        }

        list.removeNode(node);
        table.delete(key);
        currentSize--;

        return true;
    }

    public void display() {
        StringBuilder sb = new StringBuilder("[");
        Node current = list.head;
        while (current != null) {
            sb.append(current.key).append("=").append(current.data);
            if (current.next != null) {
                sb.append(", ");
            }
            current = current.next;
        }
        sb.append("]");
        System.out.println(sb);
    }

}

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Random;

/**
 * Tests for LRUCache, HashTable and DoublyLinkedList.
 *
 * Compile and run:
 *   javac HashTable.java Node.java DoublyLinkedList.java LRUCache.java LRUCacheTest.java
 *   java LRUCacheTest
 */
public class LRUCacheTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testInsertAndGet();
        testUpdateExistingKey();
        testGetMakesMostRecent();
        testFillToCapacityEvictsLeastRecent();
        testEvictionFollowsAccessOrder();
        testRemoveExistingKey();
        testRemoveHeadMiddleAndTail();
        testRemoveMissingKey();
        testDisplayOrder();
        testEmptyCache();
        testCapacityOne();
        testCapacityZeroRejected();
        testLargeCapacity();
        testRepeatedAccess();
        testHashCollisions();
        testNegativeAndMinValueKeys();
        testHashTableFullDoesNotCorrupt();
        testRandomOperationsMatchReferenceModel();

        System.out.println();
        System.out.println("Passed: " + passed + "   Failed: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    // ---------- helpers ----------

    private static void check(boolean condition, String description) {
        if (condition) {
            passed++;
            System.out.println("PASS  " + description);
        } else {
            failed++;
            System.out.println("FAIL  " + description);
        }
    }

    /** Captures what cache.display() prints, so the order can be compared as text. */
    private static String captureDisplay(LRUCache cache) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        try {
            cache.display();
        } finally {
            System.setOut(original);
        }
        return buffer.toString().trim();
    }

    // ---------- tests ----------

    private static void testInsertAndGet() {
        LRUCache cache = new LRUCache(3);
        cache.put(1, 10);
        cache.put(2, 20);
        check(cache.get(1) == 10, "insert/get: key 1 returns 10");
        check(cache.get(2) == 20, "insert/get: key 2 returns 20");
        check(cache.get(3) == -1, "insert/get: missing key 3 returns -1");
        check(cache.currentSize == 2, "insert/get: size is 2");
    }

    private static void testUpdateExistingKey() {
        LRUCache cache = new LRUCache(3);
        cache.put(1, 10);
        cache.put(2, 20);
        cache.put(1, 99);
        check(cache.currentSize == 2, "update: size unchanged (no duplicate node)");
        check(cache.get(1) == 99, "update: key 1 now returns 99");
        check(captureDisplay(cache).equals("[1=99, 2=20]"), "update: updated key is most recent");
    }

    private static void testGetMakesMostRecent() {
        LRUCache cache = new LRUCache(3);
        cache.put(1, 10);
        cache.put(2, 20);
        cache.put(3, 30);
        cache.get(1);
        check(captureDisplay(cache).equals("[1=10, 3=30, 2=20]"), "get: accessed key moves to front");
    }

    private static void testFillToCapacityEvictsLeastRecent() {
        LRUCache cache = new LRUCache(3);
        cache.put(1, 10);
        cache.put(2, 20);
        cache.put(3, 30);
        check(cache.currentSize == 3, "capacity: full at 3 entries");
        cache.put(4, 40);
        check(cache.currentSize == 3, "capacity: size stays 3 after insert into full cache");
        check(captureDisplay(cache).equals("[4=40, 3=30, 2=20]"), "eviction: least recent key 1 removed from list");
        check(cache.get(1) == -1, "eviction: key 1 gone from hash table too");
    }

    private static void testEvictionFollowsAccessOrder() {
        LRUCache cache = new LRUCache(3);
        cache.put(1, 10);
        cache.put(2, 20);
        cache.put(3, 30);
        cache.get(1);       // order is now 1, 3, 2
        cache.put(4, 40);   // should evict 2
        check(captureDisplay(cache).equals("[4=40, 1=10, 3=30]"), "eviction: accessing key 1 saved it, key 2 evicted");
        check(cache.get(2) == -1, "eviction: key 2 is gone");
    }

    private static void testRemoveExistingKey() {
        LRUCache cache = new LRUCache(3);
        cache.put(1, 10);
        cache.put(2, 20);
        cache.put(3, 30);
        check(cache.remove(2), "remove: existing key 2 returns true");
        check(cache.currentSize == 2, "remove: size decremented to 2");
        check(cache.get(2) == -1, "remove: key 2 no longer retrievable");
        check(captureDisplay(cache).equals("[3=30, 1=10]"), "remove: remaining order is correct");
    }

    private static void testRemoveHeadMiddleAndTail() {
        LRUCache cache = new LRUCache(5);
        for (int k = 1; k <= 4; k++) {
            cache.put(k, k * 10);
        }
        cache.remove(3);
        check(captureDisplay(cache).equals("[4=40, 2=20, 1=10]"), "remove middle node keeps list intact");
        cache.remove(4);
        check(captureDisplay(cache).equals("[2=20, 1=10]"), "remove head node updates head");
        cache.remove(1);
        check(captureDisplay(cache).equals("[2=20]"), "remove tail node updates tail");
        check(cache.get(2) == 20, "remove: remaining key still retrievable");
    }

    private static void testRemoveMissingKey() {
        LRUCache cache = new LRUCache(2);
        cache.put(1, 10);
        check(!cache.remove(99), "remove missing: returns false for absent key");
        check(cache.currentSize == 1, "remove missing: size unchanged");
        check(cache.remove(1), "remove: key 1 removed");
        check(!cache.remove(1), "remove: second removal of key 1 returns false");
        check(cache.currentSize == 0, "remove: size back to 0");
    }

    private static void testDisplayOrder() {
        LRUCache cache = new LRUCache(4);
        for (int k = 1; k <= 4; k++) {
            cache.put(k, k * 10);
        }
        cache.get(2);
        cache.put(3, 33);
        check(captureDisplay(cache).equals("[3=33, 2=20, 4=40, 1=10]"), "display: shows most to least recent");
    }

    private static void testEmptyCache() {
        LRUCache cache = new LRUCache(5);
        check(cache.get(1) == -1, "empty: get returns -1");
        check(!cache.remove(1), "empty: remove returns false");
        check(captureDisplay(cache).equals("[]"), "empty: display prints []");
        check(cache.currentSize == 0, "empty: size is 0");
    }

    private static void testCapacityOne() {
        LRUCache cache = new LRUCache(1);
        cache.put(1, 10);
        cache.put(2, 20);
        check(cache.get(1) == -1, "capacity 1: inserting key 2 evicts key 1");
        check(cache.get(2) == 20, "capacity 1: key 2 retrievable");
        cache.put(2, 21);
        check(cache.currentSize == 1 && captureDisplay(cache).equals("[2=21]"), "capacity 1: update keeps size 1");
    }

    private static void testCapacityZeroRejected() {
        boolean threw = false;
        try {
            new LRUCache(0);
        } catch (IllegalArgumentException e) {
            threw = true;
        }
        check(threw, "capacity 0: constructor rejects it with IllegalArgumentException");
    }

    private static void testLargeCapacity() {
        // The original fixed 11-slot table overflowed once more than 11 keys were stored.
        LRUCache cache = new LRUCache(100);
        for (int k = 0; k < 100; k++) {
            cache.put(k, k * 10);
        }
        check(cache.currentSize == 100, "large capacity: 100 entries stored");
        check(cache.get(0) == 0 && cache.get(11) == 110 && cache.get(99) == 990,
              "large capacity: no entries lost past 11");
        cache.put(100, 1000);
        check(cache.currentSize == 100 && cache.get(1) == -1,
              "large capacity: least recent key 1 evicted at capacity 100");
    }

    private static void testRepeatedAccess() {
        LRUCache cache = new LRUCache(3);
        cache.put(1, 10);
        cache.put(2, 20);
        boolean allCorrect = true;
        for (int i = 0; i < 5; i++) {
            if (cache.get(1) != 10) {
                allCorrect = false;
            }
        }
        check(allCorrect, "repeated access: key 1 returns 10 on every get");
        check(cache.currentSize == 2, "repeated access: size unchanged");
        check(captureDisplay(cache).equals("[1=10, 2=20]"), "repeated access: key 1 stays most recent");
        cache.put(3, 30);
        check(cache.currentSize == 3 && cache.get(2) == 20, "repeated access: no premature eviction");
    }

    private static void testHashCollisions() {
        // Capacity 3 gives a 7-slot table, so keys 0, 7 and 14 all hash to slot 0.
        LRUCache cache = new LRUCache(3);
        cache.put(0, 100);
        cache.put(7, 700);
        cache.put(14, 1400);
        check(cache.get(0) == 100 && cache.get(7) == 700 && cache.get(14) == 1400,
              "collisions: all three colliding keys retrievable");
        check(cache.remove(7), "collisions: remove an entry in the middle of a probe chain");
        check(cache.get(14) == 1400 && cache.get(0) == 100,
              "collisions: keys after a removed entry are still found");
        cache.put(21, 2100);
        check(cache.get(21) == 2100 && cache.currentSize == 3,
              "collisions: new colliding key inserted into freed slot");
    }

    private static void testNegativeAndMinValueKeys() {
        LRUCache cache = new LRUCache(3);
        cache.put(-1, 1);
        cache.put(-8, 8);
        cache.put(Integer.MIN_VALUE, 2);
        check(cache.get(-1) == 1, "negative keys: -1 retrievable");
        check(cache.get(-8) == 8, "negative keys: -8 retrievable");
        check(cache.get(Integer.MIN_VALUE) == 2, "negative keys: Integer.MIN_VALUE retrievable");
    }

    private static void testHashTableFullDoesNotCorrupt() {
        HashTable t = new HashTable(2);
        Node a = new Node(1, 10);
        Node b = new Node(3, 30);
        t.insert(1, a);
        t.insert(3, b);
        boolean threw = false;
        try {
            t.insert(5, new Node(5, 50));
        } catch (IllegalStateException e) {
            threw = true;
        }
        check(threw, "hash table full: inserting into a full table throws");
        check(t.search(1) == a, "hash table full: key 1 still present");
        check(t.search(3) == b, "hash table full: key 3 still present");
        check(t.search(5) == null, "hash table full: key 5 not stored");
    }

    /** Randomized comparison against a simple array-based reference model. */
    private static void testRandomOperationsMatchReferenceModel() {
        Random rng = new Random(12345);
        int mismatches = 0;
        int operations = 0;
        for (int round = 0; round < 20; round++) {
            int capacity = 1 + rng.nextInt(8);
            LRUCache cache = new LRUCache(capacity);
            RefLRU ref = new RefLRU(capacity);
            for (int op = 0; op < 2000; op++) {
                int key = rng.nextInt(24) - 8;   // keys -8..15: collisions and negatives
                int choice = rng.nextInt(10);
                if (choice < 4) {
                    int value = rng.nextInt(1000);
                    cache.put(key, value);
                    ref.put(key, value);
                } else if (choice < 8) {
                    if (cache.get(key) != ref.get(key)) {
                        mismatches++;
                    }
                } else {
                    if (cache.remove(key) != ref.remove(key)) {
                        mismatches++;
                    }
                }
                if (cache.currentSize != ref.size) {
                    mismatches++;
                }
                if (!captureDisplay(cache).equals(ref.toString())) {
                    mismatches++;
                }
                operations++;
            }
        }
        check(mismatches == 0,
              "consistency: " + operations + " random ops match reference model (mismatches: " + mismatches + ")");
    }

    /** Reference model: parallel arrays kept in most-recent-first order. */
    private static class RefLRU {
        final int capacity;
        final int[] keys;
        final int[] values;
        int size = 0;

        RefLRU(int capacity) {
            this.capacity = capacity;
            this.keys = new int[capacity];
            this.values = new int[capacity];
        }

        private int indexOf(int key) {
            for (int i = 0; i < size; i++) {
                if (keys[i] == key) {
                    return i;
                }
            }
            return -1;
        }

        private void moveToFront(int i) {
            int k = keys[i];
            int v = values[i];
            for (int j = i; j > 0; j--) {
                keys[j] = keys[j - 1];
                values[j] = values[j - 1];
            }
            keys[0] = k;
            values[0] = v;
        }

        private void insertFront(int key, int value) {
            for (int j = size; j > 0; j--) {
                keys[j] = keys[j - 1];
                values[j] = values[j - 1];
            }
            keys[0] = key;
            values[0] = value;
            size++;
        }

        void put(int key, int value) {
            int i = indexOf(key);
            if (i >= 0) {
                values[i] = value;
                moveToFront(i);
                return;
            }
            if (size == capacity) {
                size--;   // drop the least recent (last) entry
            }
            insertFront(key, value);
        }

        int get(int key) {
            int i = indexOf(key);
            if (i < 0) {
                return -1;
            }
            moveToFront(i);
            return values[0];
        }

        boolean remove(int key) {
            int i = indexOf(key);
            if (i < 0) {
                return false;
            }
            for (int j = i; j < size - 1; j++) {
                keys[j] = keys[j + 1];
                values[j] = values[j + 1];
            }
            size--;
            return true;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(keys[i]).append("=").append(values[i]);
            }
            return sb.append("]").toString();
        }
    }
}

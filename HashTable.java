class HashTable {
    private int size;
    private int[] keys;
    private Node[] nodes;
    private boolean[] occupied;
    private boolean[] deleted;

    HashTable() {
        this(11);
    }

    HashTable(int size) {
        this.size = size;
        this.keys = new int[size];
        this.nodes = new Node[size];
        this.occupied = new boolean[size];
        this.deleted = new boolean[size];
    }

    private int hash(int key) {
        return Math.abs(key % size);
    }

    public void insert(int key, Node node) {
        int index = hash(key);
        int start = index;
        int firstDeleted = -1;

        while (occupied[index] || deleted[index]) {
            if (occupied[index] && keys[index] == key) {
                nodes[index] = node;
                return;
            }

            if (!occupied[index] && deleted[index] && firstDeleted == -1) {
                firstDeleted = index;
            }

            index = (index + 1) % size;

            if (index == start) {
                break;
            }
        }

        if (firstDeleted != -1) {
            index = firstDeleted;
        } else if (occupied[index]) {
            // Every slot is occupied and the key is not present.
            throw new IllegalStateException("hash table is full");
        }

        keys[index] = key;
        nodes[index] = node;
        occupied[index] = true;
        deleted[index] = false;
    }

    public Node search(int key) {
        int index = hash(key);
        int start = index;

        while (occupied[index] || deleted[index]) {
            if (occupied[index] && keys[index] == key) {
                return nodes[index];
            }
            index = (index + 1) % size;
            if (index == start) break;
        }
        return null;
    }

    public void delete(int key) {
        int index = hash(key);
        int start = index;

        while (occupied[index] || deleted[index]) {
            if (occupied[index] && keys[index] == key) {
                occupied[index] = false;
                deleted[index] = true;
                nodes[index] = null;
                return;
            }
            index = (index + 1) % size;
            if (index == start) break;
        }
    }
}

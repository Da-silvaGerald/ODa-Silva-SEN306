public class LinkedQueue implements QueueADT{
    private Node head;
    private Node tail;
    private int count;

    @override
    public void enqueue(int element) {
        Node newNode = new Node(element);
        if (isEmpty()) {
            head = tail = newNode;
        }else {
            tail.next = newNode;
            tail = newNode;
        }
        count++;
    }

    @override
    public int dequeue() {
        if (isEmpty()) throw new RuntimeException("Empty");
        int value = head.data;
        head = head.next;
        if (head == null) tail = null;
        count --;
        return value;
    }

    @override
    public boolean isEmpty() { return count == 0; }

    @override
    public int size() { return count; }
}
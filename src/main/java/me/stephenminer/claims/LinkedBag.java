package me.stephenminer.claims;

public class LinkedBag<T> {
    private int entries;
    private Node firstNode;
    public LinkedBag(){
        entries = 0;
    }

    public void add(T toAdd){
        if (firstNode == null){
            firstNode = new Node(toAdd);
        }else{
            Node current = firstNode;
            while (current.next() != null){
                current = current.next();
            }
            current.setNext(new Node(toAdd));
        }
        entries++;
    }

    private Node getNodeReference(T reference){
        if (firstNode == null) return null;
        Node current = firstNode;
        boolean found = false;
        while (!found && current != null){
            if (current.data().equals(reference))found = true;
            else current = current.next();
        }
        return current;
    }

    public boolean remove(T toRemove){
        boolean result = false;
        Node reference = getNodeReference(toRemove);
        if (reference != null) {
            reference.setData(firstNode.data());
            firstNode = firstNode.next();
            entries--;
            result = true;
        }
        return result;
    }

    //returns first node in the chain allowing you to have a while-loop iteration
    public Node iterate(){
        return firstNode;
    }




    public class Node{
        private T data;
        private Node next;
        public Node(T data){
            this(data, null);
        }
        public Node(T data, Node next){
            this.data = data;
            this.next = next;
        }

        public T data(){ return data; }
        public Node next(){ return next; }

        public void setData(T data){
            this.data = data;
        }
        public void setNext(Node next){
            this.next = next;
        }


    }
}

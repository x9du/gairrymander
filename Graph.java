package gairrymander;

import java.util.*;

// Generic undirected graph
public class Graph<T> {
    private Map<T, List<T>> map = new HashMap<>();
    
    // Adds a new isolated vertex to the graph
    public void addVertex(T s) {
        map.put(s, new LinkedList<T>());
    }

    // Adds a new vertex with list of adjacent vertices to the graph
    public void addVertex(T s, List<T> adj) {
        map.put(s, adj);
    }
    
    // Adds the edge between source to destination
    public void addEdge(T source, T destination) { 
        if (!map.containsKey(source))
            addVertex(source);
    
        if (!map.containsKey(destination))
            addVertex(destination);
    
        map.get(source).add(destination);
        map.get(destination).add(source);
    }
    
    // Returns the number of vertices
    public int getVertexCount() {
        return map.keySet().size();
    }
    
    // Returns the number of edges
    public int getEdgesCount() {
        int count = 0;
        for (T v : map.keySet()) {
            count += map.get(v).size();
        }
        return count / 2;
    }

    public List<T> getAdj(T s) {
        return map.get(s);
    }

    // Returns arbitrary root vertex
    public T getRoot() {
        for (T s : map.keySet()) {
            return s;
        }
        return null;
    }
    
    // Returns true if a vertex is present.
    public boolean hasVertex(T s) {
        return map.containsKey(s);
    }
    
    // Returns true if an edge is present.
    public boolean hasEdge(T s, T d) {
        return map.get(s).contains(d);
    }
    
    // Prints the adjancency list of each vertex.
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
    
        for (T v : map.keySet()) {
            builder.append(v.toString() + ": ");
            for (T w : map.get(v)) {
                builder.append(w.toString() + " ");
            }
            builder.append("\n");
        }
    
        return (builder.toString());
    }
}

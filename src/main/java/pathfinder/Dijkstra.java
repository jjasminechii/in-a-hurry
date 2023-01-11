package pathfinder;
import java.util.*;
import graph.Graph;
import pathfinder.datastructures.Path;

// Finds the shortest path from a starting node to an ending node
// using Dijkstra's algorithm where it uses Dijkstra's so no negative-edge
// weights are allowed and ties are broken arbitrarily.
public class Dijkstra {
    /**
     * Finds the shortest path from one node to another using Dijkstra's Algorithm
     *  so no negative-edge weights are allowed and ties are broken arbitrarily.
     * @param current graph that is being used
     * @param start beginning location to start
     * @param dest end location for the path
     * @param <N> node data type
     * @spec.requires current, start, dest is not null
     * @return shortest path from begin to end
     */
    public static <N> Path<N> calculateDijkstra(Graph<N, Double> current, N start, N dest){
        // Priority queue for cost of paths
        Queue<Path<N>> active = new PriorityQueue<>(
                new Comparator<Path<N>>() {
                    @Override
                    public int compare(Path<N> o1, Path<N> o2) {
                        return Double.compare(o1.getCost(), o2.getCost());
                    }
                }
        );
        // Set of nodes that we have found the minimum cost from start to finish
        HashSet<N> finished = new HashSet<N>();
        // Adds a path from start to itself to active
        active.add(new Path<N>(start));

        // While active is not empty
        while(!active.isEmpty()){
            // Set minPath to be the lowest-cost path in active
            Path<N> minPath = active.remove();
            // minDest is the destination node in minPath
            N minDest = minPath.getEnd();
            // If minDest is already in destination node, we return the minPath
            if(minDest.equals(dest)){
                return minPath;
            // Continue if minPath is already in finished(Set of nodes that
            // we have found the minimum cost from start to finish)
            } if(finished.contains(minDest)){
                continue;
            }
            // Finds all the children in minDest
            // If we haven't found the minimum-cost path from start to child,
            // we examine the path that we have found.
            Map<N, Set<Double>> find = current.listChildren(minDest);
            for(N nodeParent : find.keySet()) {
                for(Double child  : find.get(nodeParent)) {
                    // Add path to active if the child is not in finished
                    if(!finished.contains(child)){
                        Path<N> newPath = minPath.extend(nodeParent, child);
                        active.add(newPath);
                    }
                }
            }
            // Adds minimum destination to finished
            finished.add(minDest);
        }
        // No path from start to dest if the loop terminates.
        return null;
    }
}

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class Main {

    private int highestPeak = 0;
    private int numPaths = 0;
    private int globalShortestPath = Integer.MAX_VALUE;
    private int[][] trailDistances;
    private Node[] pointsOfInterest;
    private List<Stack<Node>> shortestPaths;

    public static void main(String[] args) {
        Main m = new Main();
        m.init();
    }


    private void init() {
        /*
         * If using Eclipse to test System.in via input redirection,
         * set this to true;
         */
        boolean readFromSystemIn = false;

        Scanner scanner = null;
        if (readFromSystemIn) {
            scanner = new Scanner(System.in);
        } else {
            try {
                scanner = new Scanner(new File("input/input.txt"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (scanner == null) System.exit(1);

        int numPoints = scanner.nextInt();
        highestPeak = numPoints - 1;

        // Initialize point of interest nodes
        pointsOfInterest = new Node[numPoints];
        for (int i = 0; i < numPoints; i++) {
            Node newNode = new Node(i);
            pointsOfInterest[i] = newNode;
        }

        trailDistances = new int[numPoints][numPoints];
        int[][] pathCounted = new int[numPoints][numPoints];

        int numTrails = scanner.nextInt();
        for (int i = 0; i < numTrails; i++) {
            int pointA = scanner.nextInt();
            int pointB = scanner.nextInt();
            int distance = scanner.nextInt();

            // don't want to include an edge that loops to itself
            if (pointA == pointB) continue;

            Node startNode = pointsOfInterest[pointA];
            Node destNode = pointsOfInterest[pointB];

            startNode.myAdjacentNodes.add(destNode);
            destNode.myAdjacentNodes.add(startNode);

            // record the number of trails going to this location
            pathCounted[pointA][pointB]++;

            // if there is already a value at (pointA, pointB) and
            // it's greater than 'distance', assign it to that location.
            if (trailDistances[pointA][pointB] > 0 && distance < trailDistances[pointA][pointB]) {
                trailDistances[pointA][pointB] = distance;
            }

            trailDistances[pointA][pointB] = distance;
            trailDistances[pointB][pointA] = distance;
        }
        scanner.close();

        shortestPaths = new ArrayList<>();
        shortestPaths.add(new Stack<>());
        //shortestPaths.get(numPaths).push(pointsOfInterest[0]);

        relax(pointsOfInterest[0]);

        //findShortestPaths(pointsOfInterest[highestPeak]);

        System.out.print("break here");

        // TODO: need to check if there is more input.
    }

    private void findShortestPaths(Node highestNode) {
        Node shortestDist = null;
        for(Node node : highestNode.myAdjacentNodes) {


            if (node.visited) {
                if (shortestDist == null) {
                    shortestDist = node;
                } else if (node.myAbsoluteShortestPath < shortestDist.myAbsoluteShortestPath){
                    shortestDist = node;
                }
            }
        }
    }

    // TODO: how to keep track of shortest paths?
    private void relax(Node destNode) {
        // base case.
        if (destNode == null) return;

        destNode.visited = true;
        shortestPaths.get(numPaths).push(destNode);

        while (!destNode.allAdjacentVisited) {

            // get the shortest distance adjacent node.
            Node shortestNode = destNode.updateAdjacentEdges();

            // if we've reached the highest point, check if it's less
            // than global shortest path and return back up the recursive tree.
            if (shortestNode != null && shortestNode.myPoint == highestPeak) {
                if (shortestNode.myShortestPath <= globalShortestPath) {
                    globalShortestPath = shortestNode.myShortestPath;
                }
                return;
            }
            relax(shortestNode);
        }
        shortestPaths.get(numPaths).pop();
    }

    private void startNewPath() {
        Stack<Node> clone = (Stack<Node>) shortestPaths.get(numPaths).clone();
        shortestPaths.add(clone);
        numPaths++;
    }


    private class Node implements Comparable<Node> {
        List<Node> myAdjacentNodes;
        int myPoint;
        int myShortestPath;
        int myAbsoluteShortestPath;
        boolean visited;
        boolean allAdjacentVisited;

        Node(int point) {
            this.myPoint = point;
            myShortestPath = 0;
            myAbsoluteShortestPath = 0;
            myAdjacentNodes = new ArrayList<>();
        }

        // returns the node with the shortest path after updating shortest path to each edge.
        Node updateAdjacentEdges() {
            Node shortestAdjacentNode = null;
            allAdjacentVisited = true;

            for (Node node : myAdjacentNodes) {

                if (node.myPoint == highestPeak) {
                    shortestPaths.get(numPaths).push(node);
                    startNewPath();
                }

                // not concerned with entrance node
                if (node.myPoint == 0) continue;

                int distToNode = trailDistances[this.myPoint][node.myPoint] + this.myShortestPath;

                // shortest path hasn't been set or is less than the current one
                if (node.myShortestPath == 0 || distToNode < node.myShortestPath) {
                    node.myShortestPath = distToNode;
                }


                // if it hasn't been visited, check if it's shorter than the current path
                if (!node.visited) {
                    allAdjacentVisited = false;
                    if (shortestAdjacentNode == null) {
                        shortestAdjacentNode = node;
                    }
                    // if it's path is shorter than the current, choose that one.
                    else if (trailDistances[this.myPoint][node.myPoint] <
                            trailDistances[this.myPoint][shortestAdjacentNode.myPoint]) {
                        shortestAdjacentNode = node;
                    }
                }
            }

            if (allAdjacentVisited) {
                myAbsoluteShortestPath = myShortestPath;
            }

            return shortestAdjacentNode;
        }

        @Override
        public int compareTo(Node otherNode) {
            if (this.myShortestPath > otherNode.myShortestPath) return -1;
            else if (this.myShortestPath == otherNode.myShortestPath) return 0;
            else return 1;
        }
    }


}

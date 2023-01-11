/*
 * Copyright (C) 2022 Kevin Zatloukal.  All rights reserved.  Permission is
 * hereby granted to students registered for University of Washington
 * CSE 331 for use solely during Spring Quarter 2022 for purposes of
 * the course.  No other use, copying, distribution, or modification
 * is permitted without prior written consent. Copyrights for
 * third-party components of this work must be honored.  Instructors
 * interested in reusing these course materials should contact the
 * author.
 */

package pathfinder.scriptTestRunner;

import graph.Graph;
import pathfinder.datastructures.Path;

import java.io.*;
import java.util.*;

import static pathfinder.Dijkstra.calculateDijkstra;

/**
 * This class implements a test driver that uses a script file format
 * to test an implementation of Dijkstra's algorithm on a graph.
 */
public class PathfinderTestDriver {
    private final Map<String, Graph<String, Double>> graphs = new HashMap<String, Graph<String,Double>>();
    private final PrintWriter output;
    private final BufferedReader input;

    // Leave this constructor public
    public PathfinderTestDriver(Reader r, Writer w) {
        // TODO: Implement this, reading commands from `r` and writing output to `w`.
        // See GraphTestDriver as an example.
        input = new BufferedReader(r);
        output = new PrintWriter(w);
    }

    public void runTests() throws IOException {
        String inputLine;
        while((inputLine = input.readLine()) != null) {
            if((inputLine.trim().length() == 0) ||
                    (inputLine.charAt(0) == '#')) {
                // echo blank and comment lines
                output.println(inputLine);
            } else {
                // separate the input line on white space
                StringTokenizer st = new StringTokenizer(inputLine);
                if(st.hasMoreTokens()) {
                    String command = st.nextToken();

                    List<String> arguments = new ArrayList<>();
                    while(st.hasMoreTokens()) {
                        arguments.add(st.nextToken());
                    }

                    executeCommand(command, arguments);
                }
            }
            output.flush();
        }
    }

    private void executeCommand(String command, List<String> arguments) {
        try {
            switch(command) {
                case "CreateGraph":
                    createGraph(arguments);
                    break;
                case "AddNode":
                    addNode(arguments);
                    break;
                case "AddEdge":
                    addEdge(arguments);
                    break;
                case "ListNodes":
                    listNodes(arguments);
                    break;
                case "ListChildren":
                    listChildren(arguments);
                    break;
                case "FindPath":
                    findPath(arguments);
                    break;
                default:
                    output.println("Unrecognized command: " + command);
                    break;
            }
        } catch(Exception e) {
            String formattedCommand = command;
            formattedCommand += arguments.stream().reduce("", (a, b) -> a + " " + b);
            output.println("Exception while running command: " + formattedCommand);
            e.printStackTrace(output);
        }
    }

    private void createGraph(List<String> arguments) {
        if(arguments.size() != 1) {
            throw new CommandException("Bad arguments to CreateGraph: " + arguments);
        }

        String graphName = arguments.get(0);
        createGraph(graphName);
    }

    private void createGraph(String graphName) {
        // TODO Insert your code here.

        graphs.put(graphName, new Graph<String,Double>());
        output.println("created graph " + graphName);
    }

    private void addNode(List<String> arguments) {
        if(arguments.size() != 2) {
            throw new CommandException("Bad arguments to AddNode: " + arguments);
        }

        String graphName = arguments.get(0);
        String nodeName = arguments.get(1);

        addNode(graphName, nodeName);
    }

    private void addNode(String graphName, String nodeName) {
        // TODO Insert your code here.

        Graph<String,Double> possible = graphs.get(graphName);
        possible.addNode(nodeName);
        output.println("added node " + nodeName + " to " + graphName);
    }

    private void addEdge(List<String> arguments) {
        if(arguments.size() != 4) {
            throw new CommandException("Bad arguments to AddEdge: " + arguments);
        }

        String graphName = arguments.get(0);
        String parentName = arguments.get(1);
        String childName = arguments.get(2);
        Double edgeLabel = Double.parseDouble(arguments.get(3));

        addEdge(graphName, parentName, childName, edgeLabel);
    }

    private void addEdge(String graphName, String parentName, String childName,
                         Double edgeLabel) {
        // TODO Insert your code here.

        Graph<String,Double> possible = graphs.get(graphName);
        possible.addEdge(parentName, childName, edgeLabel);
        String alter = String.format("%.3f", edgeLabel);
        output.println("added edge " + alter + " from "
                + parentName + " to " + childName + " in " + graphName);
    }

    private void listNodes(List<String> arguments) {
        if(arguments.size() != 1) {
            throw new CommandException("Bad arguments to ListNodes: " + arguments);
        }

        String graphName = arguments.get(0);
        listNodes(graphName);
    }

    private void listNodes(String graphName) {
        // TODO Insert your code here.

        Graph<String,Double> current = graphs.get(graphName);
        String update = graphName + " contains:";
        TreeSet<String> organize = new TreeSet<>(current.listNodes());
        for(String node : organize){
            update += " " + node;
        }
        output.println(update);
    }

    private void listChildren(List<String> arguments) {
        if(arguments.size() != 2) {
            throw new CommandException("Bad arguments to ListChildren: " + arguments);
        }

        String graphName = arguments.get(0);
        String parentName = arguments.get(1);
        listChildren(graphName, parentName);
    }

    private void listChildren(String graphName, String parentName) {
        // TODO Insert your code here.

        Graph<String,Double> current = graphs.get(graphName);
        String update = "the children of " + parentName + " in " + graphName + " are:";
        //Set<String> loop = current.listChildren(parentName).keySet();
        for (String currentNode : current.listChildren(parentName).keySet()) {
            TreeSet<Double> hold = new TreeSet<>(current.listChildren(parentName).get(currentNode));
            for (Double currentEdge : hold) {
                update += " " + currentNode + "(" + currentEdge + ")";
            }
        }
        output.println(update);
    }

    private void findPath(List<String> arguments){
        if(arguments.size() != 3){
            throw new CommandException("Bad arguments to FindPath: " + arguments);
        }
        String graphName = arguments.get(0);
        String parentName = arguments.get(1);
        String childName = arguments.get(2);
        findPath(graphName, parentName, childName);
    }

    private void findPath(String graphName, String parentName, String childName){
        Graph<String,Double> current = graphs.get(graphName);
        if(!current.listNodes().contains(parentName) || !current.listNodes().contains(childName)){
            if(!current.listNodes().contains(parentName)){
                output.println("unknown: " + parentName);
            } else {
                output.println("unknown: " + childName);
            }
        }
        else {
            output.println("path from " + parentName + " to " + childName + ":");
            Path<String> path = calculateDijkstra(current, parentName, childName);
            if (path == null) {
                output.println("no path found");
            } else {
                Set<String> currentNode = new TreeSet<>();
                for (Path<String>.Segment pathWeight : path) {
                    Double cost = pathWeight.getCost();
                    childName = pathWeight.getEnd();
                    currentNode.add(childName);
                    String edgeName = String.format("%.3f", cost);
                    output.println(parentName + " to " + childName + " with weight " + edgeName);
                    parentName = childName;
                }
                output.println("total cost: " + String.format("%.3f", path.getCost()));
            }
        }
    }

    /**
     * This exception results when the input file cannot be parsed properly
     **/
    static class CommandException extends RuntimeException {

        public CommandException() {
            super();
        }

        public CommandException(String s) {
            super(s);
        }

        public static final long serialVersionUID = 3495;
    }
}

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

package pathfinder;

import graph.Graph;
import pathfinder.datastructures.Path;
import pathfinder.datastructures.Point;
import pathfinder.parser.CampusBuilding;
import pathfinder.parser.CampusPath;
import pathfinder.parser.CampusPathsParser;

import java.util.*;

public class CampusMap implements ModelAPI {
    public static final boolean DEBUG = false;
    // Representation Invariant RI =
    // The CampusMap is not null.
    // campus!= null
    // shortLong != null
    // shortPoint != null
    // all keys and values in shortLong and shortPoint are not null

    // Abstraction Function AF(this) = A CampusPath where
    // campus = graph that represents campusPaths
    // shortLong = a map that maps a building's abbreviated names to longer names
    // shortPoint = a map that maps the building's short names to its point locations(x,y)
    private final Graph<Point, Double> campus;
    private final Map<String, String> shortLong;
    private final Map<String, Point> shortPoint;

    /**
     * Constructs a CampusMap using the path data and the campus building data
     * @spec.requires "campus_paths.csv" is not null
     * @spec.requires "campus_buildings.csv" is not null
     * @spec.effects Constructs a CampusMap in a graph that stores
     * the buildings and path data.
     */
    public CampusMap(){
        checkRep();
        campus = new Graph<>();
        shortLong = new HashMap<>();
        shortPoint = new HashMap<>();
        List<CampusPath> listOfPaths = CampusPathsParser.parseCampusPaths("campus_paths.csv");
        List<CampusBuilding> listOfBuildings = CampusPathsParser.parseCampusBuildings("campus_buildings.csv");
        parseBuildings(listOfBuildings);
        parsePaths(listOfPaths);
        checkRep();
    }

    public void parsePaths(List<CampusPath> paths){
        checkRep();
        for(CampusPath update : paths){
            Point first = new Point(update.getX1(), update.getY1());
            Point second = new Point(update.getX2(), update.getY2());
            campus.addNode(first);
            campus.addNode(second);
            campus.addEdge(first, second, update.getDistance());
        }
        checkRep();
    }

    public void parseBuildings(List<CampusBuilding> buildings){
        checkRep();
        for(CampusBuilding current: buildings){
            Point start = new Point(current.getX(), current.getY());
            shortLong.put(current.getShortName(), current.getLongName());
            shortPoint.put(current.getShortName(), start);
            campus.addNode(start);
        }
        checkRep();
    }

    @Override
    public boolean shortNameExists(String shortName) {
        // TODO: Implement this method exactly as it is specified in ModelAPI
        checkRep();
        return shortLong.containsKey(shortName);
    }

    @Override
    public String longNameForShort(String shortName) {
        // TODO: Implement this method exactly as it is specified in ModelAPI
        checkRep();
        if (!shortLong.containsKey(shortName)) {
            throw new IllegalArgumentException();
        }
        checkRep();
        return shortLong.get(shortName);
    }

    @Override
    public Map<String, String> buildingNames() {
        // TODO: Implement this method exactly as it is specified in ModelAPI
        checkRep();
        return Collections.unmodifiableMap(shortLong);
    }

    @Override
    public Path<Point> findShortestPath(String startShortName, String endShortName) {
        // TODO: Implement this method exactly as it is specified in ModelAPI
        checkRep();
        if(startShortName == null || endShortName == null ||
           startShortName == null && endShortName == null ||
           !(shortLong.containsKey(startShortName)) || !shortLong.containsKey(endShortName)){
            throw new IllegalArgumentException();
        }
        Point start = shortPoint.get(startShortName);
        Point end = shortPoint.get(endShortName);
        checkRep();
        return Dijkstra.calculateDijkstra(campus, start, end);
    }
    private void checkRep(){
        if(DEBUG){
            if(campus == null){
                throw new RuntimeException("campus can't be null!");
            }
            if(shortLong == null){
                throw new RuntimeException("shortLong can't be null!");
            }
            if(shortPoint == null){
                throw new RuntimeException("shortPoint can't be null!");
            }
            for(String shortNames : shortLong.keySet()){
                if(shortNames == null){
                    throw new RuntimeException("shortLong keys can't be null!");
                }
                if(shortLong.get(shortNames) == null){
                    throw new RuntimeException("shortLong values can't be null!");
                }
            }
            for(String shortPointNames : shortPoint.keySet()){
                if(shortPointNames == null){
                    throw new IllegalArgumentException("shortPoint keys can't be null!");
                }
                if(shortPoint.get(shortPointNames) == null){
                    throw new IllegalArgumentException("shortPoint values can't be null!");
                }
            }
        }
    }

}

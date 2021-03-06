/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package Utilities;

import Entities.AccessPoint;
import Entities.AccessPointModel;
import Entities.Obstacle;
import Forms.Canvas;
import Forms.OptimalityProgress;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JProgressBar;
import org.apache.batik.ext.awt.geom.Polygon2D;

/**
 *
 * @author JAD4
 */
public class BruteForceCalculation extends Thread {
    
    private final String threadName = "Brute Force Calculation";
    private Thread thread;
    private final ArrayList<Obstacle> outerWalls = new ArrayList<Obstacle>();
    private Polygon2D polygon;
    private ArrayList<Obstacle> obstacles;
    private final Canvas canvas;
    private JProgressBar pbOptimalLocation;
    private AccessPointModel apModel;
    private int progress = 0;
    private OptimalityProgress dialog;
    private boolean isCancelled = false;
    private final double minThreshold = Constants.isLaptop ? PathLossModels.MIN_THRESHOLDLAPTOP : PathLossModels.MIN_THRESHOLDMOBILE;
    private boolean isLocationOptimal = false;
    
    public BruteForceCalculation(Canvas canvas, JProgressBar pbOptimalLocation, AccessPointModel apModel, OptimalityProgress dialog) {
        this.canvas = canvas;
        this.dialog = dialog;
        this.obstacles = canvas.getObstacles();
        this.pbOptimalLocation = pbOptimalLocation;
        this.apModel = apModel;
    }
    
    @Override
    public void run() {
        System.out.println("Running thread : " + threadName);
        if(Optimality.ensureOptimality)
            findOptimalAPLocation();
        else
            //fillOuterWalls();
            findBestAPLocation();
    }
    
    public void start() {
        System.out.println("Starting thread : " + threadName);
        if (thread == null) {
            thread = new Thread(this, threadName);
            thread.start();
        }
    }
    
    private void fillOuterWalls() {
        int minT1Index, maxT1Index, index;
        float t1, maxT1, minT1;
        
        //for(Obstacle obs : obstacles)
            //obs.setOuterWall(false);
        
        if(!obstacles.isEmpty()){
            System.out.println("Obstacles: " + obstacles.size());
            Point endPoint, startPoint;
            
            for (Obstacle obs : obstacles) {
                if (obs.isOuterWall()) {
                    outerWalls.add(obs);
                }
            }
            
            for(int i = 0; i < canvas.getWidth(); i+=10){
                minT1 = 2; maxT1 = -1; t1 = -1;
                maxT1Index = -1; minT1Index = -1;
                
                startPoint = new Point(i, 0);
                endPoint = new Point(i, canvas.getHeight());
                
                for (index = 0; index < obstacles.size(); index++) {
                    t1 = PathLossModels.getSegmentsIntersection(startPoint, endPoint, obstacles.get(index));
                    
                    if (t1 > -10) {
                        if (maxT1 < t1) {
                            maxT1 = t1;
                            maxT1Index = index;
                        }
                        if (minT1 > t1) {
                            minT1 = t1;
                            minT1Index = index;
                        }
                    }
                }
                
                if (maxT1Index > -1 && !obstacles.get(maxT1Index).isOuterWall()) {
                    obstacles.get(maxT1Index).setOuterWall(true);
                    outerWalls.add(obstacles.get(maxT1Index));
                }
                if (minT1Index > -1 && !obstacles.get(minT1Index).isOuterWall()) {
                    obstacles.get(minT1Index).setOuterWall(true);
                    outerWalls.add(obstacles.get(minT1Index));
                }
            }
            for(int i = 0; i < canvas.getHeight(); i+=10){
                minT1 = 2; maxT1 = -1; t1 = -1;
                maxT1Index = -1; minT1Index = -1;
                
                startPoint = new Point(0, i);
                endPoint = new Point(canvas.getWidth(), i);
                for (index = 0; index < obstacles.size(); index++) {
                    t1 = PathLossModels.getSegmentsIntersection(startPoint, endPoint, obstacles.get(index));
                    
                    if (t1 > -10) {
                        if (maxT1 < t1) {
                            maxT1 = t1;
                            maxT1Index = index;
                        }
                        if (minT1 > t1) {
                            minT1 = t1;
                            minT1Index = index;
                        }
                    }
                }
                
                if (maxT1Index > -1 && !obstacles.get(maxT1Index).isOuterWall()) {
                    obstacles.get(maxT1Index).setOuterWall(true);
                    outerWalls.add(obstacles.get(maxT1Index));
                }
                if (minT1Index > -1 && !obstacles.get(minT1Index).isOuterWall()) {
                    obstacles.get(minT1Index).setOuterWall(true);
                    outerWalls.add(obstacles.get(minT1Index));
                }
            }
//            Point p1 = new Point(0, 0), p2 = new Point(0, canvas.getHeight()),
//                    p3 = new Point(canvas.getWidth(), 0), p4 = new Point(canvas.getWidth(), canvas.getHeight());
//            int xEnd, yEnd;
//            for(int angle = 0; angle < 90; angle++){
//                xEnd = (int)(p1.x + 2500 * Math.cos(Math.toRadians(angle)));
//                yEnd = (int)(p1.y + 2500 * Math.sin(Math.toRadians(angle)));
//                endPoint = new Point(xEnd, yEnd);
//                
//                // Iterating on all the obstacles to choose the outer wall point
//                minT1 = 2; maxT1 = -1; t1 = -1;
//                maxT1Index = -1; minT1Index = -1;
//                
//                for (index = 0; index < obstacles.size(); index++) {
//                    t1 = PathLossModels.getSegmentsIntersection(p1, endPoint, obstacles.get(index));
//                    
//                    if (t1 > -10) {
//                        if (maxT1 < t1) {
//                            maxT1 = t1;
//                            maxT1Index = index;
//                        }
//                        
//                        if (minT1 > t1) {
//                            minT1 = t1;
//                            minT1Index = index;
//                        }
//                    }
//                }
//                
//                if (maxT1Index > -1 && !obstacles.get(maxT1Index).isOuterWall()) {
//                    obstacles.get(maxT1Index).setOuterWall(true);
//                    outerWalls.add(obstacles.get(maxT1Index));
//                }
//                if (minT1Index > -1 && !obstacles.get(minT1Index).isOuterWall()) {
//                    obstacles.get(minT1Index).setOuterWall(true);
//                    outerWalls.add(obstacles.get(minT1Index));
//                }
//                
//                xEnd = (int)(p2.x + 2500 * Math.cos(Math.toRadians(-angle)));
//                yEnd = (int)(p2.y + 2500 * Math.sin(Math.toRadians(-angle))) + canvas.getHeight();
//                endPoint = new Point(xEnd, yEnd);
//                
//                // Iterating on all the obstacles to choose the outer wall point
//                minT1 = 2; maxT1 = -1; t1 = -1;
//                maxT1Index = -1; minT1Index = -1;
//                
//                for (index = 0; index < obstacles.size(); index++) {
//                    t1 = PathLossModels.getSegmentsIntersection(p2, endPoint, obstacles.get(index));
//                    
//                    if (t1 > -10) {
//                        if (maxT1 < t1) {
//                            maxT1 = t1;
//                            maxT1Index = index;
//                        }
//                        
//                        if (minT1 > t1) {
//                            minT1 = t1;
//                            minT1Index = index;
//                        }
//                    }
//                }
//                
//                if (maxT1Index > -1 && !obstacles.get(maxT1Index).isOuterWall()) {
//                    obstacles.get(maxT1Index).setOuterWall(true);
//                    outerWalls.add(obstacles.get(maxT1Index));
//                }
//                if (minT1Index > -1 && !obstacles.get(minT1Index).isOuterWall()) {
//                    obstacles.get(minT1Index).setOuterWall(true);
//                    outerWalls.add(obstacles.get(minT1Index));
//                }
//                
//                xEnd = (int)(p3.x + 2500 * Math.cos(Math.toRadians(180 - angle))) + canvas.getWidth();
//                yEnd = (int)(p3.y + 2500 * Math.sin(Math.toRadians(180 - angle)));
//                endPoint = new Point(xEnd, yEnd);
//                
//                // Iterating on all the obstacles to choose the outer wall point
//                minT1 = 2; maxT1 = -1; t1 = -1;
//                maxT1Index = -1; minT1Index = -1;
//                
//                for (index = 0; index < obstacles.size(); index++) {
//                    t1 = PathLossModels.getSegmentsIntersection(p3, endPoint, obstacles.get(index));
//                    
//                    if (t1 > -10) {
//                        if (maxT1 < t1) {
//                            maxT1 = t1;
//                            maxT1Index = index;
//                        }
//                        
//                        if (minT1 > t1) {
//                            minT1 = t1;
//                            minT1Index = index;
//                        }
//                    }
//                }
//                
//                if (maxT1Index > -1 && !obstacles.get(maxT1Index).isOuterWall()) {
//                    obstacles.get(maxT1Index).setOuterWall(true);
//                    outerWalls.add(obstacles.get(maxT1Index));
//                }
//                if (minT1Index > -1 && !obstacles.get(minT1Index).isOuterWall()) {
//                    obstacles.get(minT1Index).setOuterWall(true);
//                    outerWalls.add(obstacles.get(minT1Index));
//                }
//                
//                xEnd = (int)(p4.x + 2500 * Math.cos(Math.toRadians(angle - 180))) + canvas.getWidth();
//                yEnd = (int)(p4.y + 2500 * Math.sin(Math.toRadians(angle - 180))) + canvas.getHeight();
//                endPoint = new Point(xEnd, yEnd);
//                
//                // Iterating on all the obstacles to choose the outer wall point
//                minT1 = 2; maxT1 = -1; t1 = -1;
//                maxT1Index = -1; minT1Index = -1;
//                
//                for (index = 0; index < obstacles.size(); index++) {
//                    t1 = PathLossModels.getSegmentsIntersection(p4, endPoint, obstacles.get(index));
//                    
//                    if (t1 > -10) {
//                        if (maxT1 < t1) {
//                            maxT1 = t1;
//                            maxT1Index = index;
//                        }
//                        
//                        if (minT1 > t1) {
//                            minT1 = t1;
//                            minT1Index = index;
//                        }
//                    }
//                }
//                
//                if (maxT1Index > -1 && !obstacles.get(maxT1Index).isOuterWall()) {
//                    obstacles.get(maxT1Index).setOuterWall(true);
//                    outerWalls.add(obstacles.get(maxT1Index));
//                }
//                if (minT1Index > -1 && !obstacles.get(minT1Index).isOuterWall()) {
//                    obstacles.get(minT1Index).setOuterWall(true);
//                    outerWalls.add(obstacles.get(minT1Index));
//                }
//            }
            int xCenter = canvas.getWidth() / 2, yCenter = canvas.getHeight() / 2;
            double angleRadians;
            Double xEnd, xStart, yEnd, yStart;
            
            // Iterating on each angle
            for (double angle = 0; angle < 180; angle++) {
                
                index = 0;
                minT1Index = -1;
                maxT1Index = -1;
                t1 = -1;
                maxT1 = -1;
                minT1 = 2;
                
                // convert degree to radian
                angleRadians = Math.toRadians(angle);
                xEnd = xCenter + 1500 * Math.cos(angleRadians);
                yEnd = yCenter + 1500 * Math.sin(angleRadians);
                xStart = xCenter - 1500 * Math.cos(angleRadians);
                yStart = yCenter - 1500 * Math.sin(angleRadians);
                
                // Start and End Point Coordinates
                endPoint = new Point(xEnd.intValue(), yEnd.intValue());
                startPoint = new Point(xStart.intValue(), yStart.intValue());
                
                // Iterating on all the obstacles to choose the outer wall point
                for (index = 0; index < obstacles.size(); index++) {
                    
                    t1 = PathLossModels.getSegmentsIntersection(startPoint, endPoint, obstacles.get(index));
                    
                    if (t1 > -10) {
                        if (maxT1 < t1) {
                            maxT1 = t1;
                            maxT1Index = index;
                        }
                        if (minT1 > t1) {
                            minT1 = t1;
                            minT1Index = index;
                        }
                    }
                }
                
                if (maxT1Index > -1 && !obstacles.get(maxT1Index).isOuterWall()) {
                    obstacles.get(maxT1Index).setOuterWall(true);
                    outerWalls.add(obstacles.get(maxT1Index));
                }
                if (minT1Index > -1 && !obstacles.get(minT1Index).isOuterWall()) {
                    obstacles.get(minT1Index).setOuterWall(true);
                    outerWalls.add(obstacles.get(minT1Index));
                }
            }
        }
        
        //canvas.setObstacles(outerWalls);
        System.out.println("Outer walls: " + outerWalls.size());
    }
    
    private void findOptimalAPLocation() {
        boolean isOptimalAPFound = false;
        
        fillOuterWalls();
        
        Point centroid = Optimality.centroid(outerWalls);
        
        System.out.println(minThreshold);
        
        AccessPoint optimalAP = new AccessPoint(centroid, "Optimal AP", apModel);
        if (Optimality.checkIfPointIsOptimal(optimalAP, obstacles)) {
            canvas.setAPOptimalLocation(optimalAP);
            isOptimalAPFound = true;
            System.out.println("Centroid is Optimal" + outerWalls.size());
        }
        else{
            
            polygon = new Polygon2D();
            
            for (int i = 0; i < outerWalls.size(); i++) {
                polygon.addPoint(outerWalls.get(i).getEndPoint().x, outerWalls.get(i).getEndPoint().y);
                polygon.addPoint(outerWalls.get(i).getStartPoint().x, outerWalls.get(i).getStartPoint().y);
            }
            
            Point optimalAPPoint;
            
            int pixelScannedNb = 0;
            
            pbOptimalLocation.setMaximum(canvas.getWidth() * canvas.getHeight());
            progress = 0;
            pbOptimalLocation.setValue(progress);
            
            int before = new Date().getSeconds();
            
            outerLoop:
            for (int a = 0; a < canvas.getWidth(); a += Constants.BRUTE_FORCE_STEP) {
                for (int b = 0; b < canvas.getHeight(); b += Constants.BRUTE_FORCE_STEP) {
                    progress++;
                    pbOptimalLocation.setValue(progress);
                    pixelScannedNb++;
                    
                    optimalAPPoint = new Point(a, b); // the expected optimal AP position
                    
                    optimalAP.setLocation(optimalAPPoint);
                    
                    if(isCancelled)
                        return;
                    
                    if (polygon.contains(optimalAPPoint) && !Optimality.isPointOverOuterWall(optimalAPPoint, obstacles)
                            && Optimality.checkIfPointIsOptimal(optimalAP, obstacles)) {
                        isOptimalAPFound = true;
                        break outerLoop;
                    } else {
                        isOptimalAPFound = false;
                    }
                    
                }
            }
            
            System.out.println("\n\n" + pixelScannedNb + " AP location scanned in " + (new Date().getSeconds() - before) + " seconds \n\n");
        }
        while(progress < pbOptimalLocation.getMaximum()){
            progress++;
            pbOptimalLocation.setValue(progress);
        }
        if (isOptimalAPFound) {
            canvas.setAPOptimalLocation(optimalAP);
            dialog.setMessage(Constants.OPTIMAL_AP_FOUND);
            System.out.println("Optimal Location Found at (" + optimalAP.getLocation().getX() + "," + optimalAP.getLocation().getY() + ")");
        } else {
            canvas.accessPoints.clear();
            canvas.drawHeatMap(false);
            dialog.setMessage(Constants.OPTIMAL_AP_NOT_FOUND);
        }
    }
    
    private void findBestAPLocation(){
        fillOuterWalls();
        
        System.out.println("Best Loc");
        
        boolean isOptimalLocFound = false;
        
        polygon = new Polygon2D();
            
        for (int i = 0; i < outerWalls.size(); i++) {
            polygon.addPoint(outerWalls.get(i).getEndPoint().x, outerWalls.get(i).getEndPoint().y);
            polygon.addPoint(outerWalls.get(i).getStartPoint().x, outerWalls.get(i).getStartPoint().y);
        }
        
        // power recevied on each pixel
        double averagePower = 0, maxAveragePower = -500, maxOptimalAvgPower = -500;
        
        Point bestPoint = new Point(0, 0), optimalAPPoint = new Point(0, 0), point;
        AccessPoint optimalAP = new AccessPoint(bestPoint, "Optimal AP", apModel);
        
        pbOptimalLocation.setMaximum(canvas.getWidth() * canvas.getHeight());
        progress = 0;
        pbOptimalLocation.setValue(progress);
        
        outerLoop:
        for (int a = 0; a < canvas.getWidth(); a += Constants.BRUTE_FORCE_STEP) {
            for (int b = 0; b < canvas.getHeight(); b += Constants.BRUTE_FORCE_STEP){
                progress++;
                pbOptimalLocation.setValue(progress);
                
                point = new Point(a, b);
                optimalAP.setLocation(point);
                
                if(isCancelled)
                    return;
                
                isLocationOptimal = true;
                if(polygon.contains(point) && !Optimality.isPointOverOuterWall(point, obstacles)){
                    averagePower = Optimality.getAverageReceivedPower(optimalAP, obstacles, this);
                    //System.out.println("AVG " + averagePower);
                    if(isLocationOptimal && averagePower > maxOptimalAvgPower){
                        maxOptimalAvgPower = averagePower;
                        isOptimalLocFound = true;
                        optimalAPPoint = point;
                        //System.out.println("optimal best " + point);
                    }
                    
                    if(averagePower > maxAveragePower){
                        maxAveragePower = averagePower;
                        bestPoint = point;
                    }
                }
            }
        }
        
        if(isOptimalLocFound)
            optimalAP.setLocation(optimalAPPoint);
        else
            optimalAP.setLocation(bestPoint);
        canvas.setAPOptimalLocation(optimalAP);
        dialog.setMessage(Constants.OPTIMAL_AP_FOUND); 
    }
    
    public void cancel() {
        isCancelled = true;
    }

    public void setIsLocationOptimal(boolean isLocationOptimal) {
        this.isLocationOptimal = isLocationOptimal;
    }
    
    
}

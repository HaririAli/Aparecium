package Utilities;

import Entities.AccessPoint;
import Entities.Obstacle;
import java.awt.Point;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

/**
 *
 * @author JAD4
 */
public class Optimality {
    
    public static boolean useBruteForce;
    public static boolean ensureOptimality;

    /**
     * This method checks if there is an intersection between "the line that
     * link the AP position and its destination" with any "outer wall"
     *
     * @param apPosition
     * @param destination
     * @param outerWalls
     * @return the intersection point if exists, otherwise return null
     */
    public static Point checkIfPointIntersectsWithOuterWall(Point apPosition, Point destination, ArrayList<Obstacle> outerWalls) {

        for (Obstacle outerWall : outerWalls) {
            if (outerWall.getStartPoint().getX() != destination.getX() && outerWall.getStartPoint().getY() != destination.getY()) {
                float dx12 = destination.x - apPosition.x;
                float dy12 = destination.y - apPosition.y;
                float dx34 = outerWall.getEndPoint().x - outerWall.getStartPoint().x;
                float dy34 = outerWall.getEndPoint().y - outerWall.getStartPoint().y;

                float denominator = (dy12 * dx34 - dx12 * dy34);

                if (denominator != 0) {
                    float t1 = ((apPosition.x - outerWall.getStartPoint().x) * dy34 + (outerWall.getStartPoint().y - apPosition.y) * dx34) / denominator;
                    float t2 = ((outerWall.getStartPoint().x - apPosition.x) * dy12 + (apPosition.y - outerWall.getStartPoint().y) * dx12) / -denominator;

                    Boolean segments_intersect = ((t1 >= 0) && (t1 <= 1) && (t2 >= 0) && (t2 <= 1));

                    if (segments_intersect == true) {
                        return new Point((int) (apPosition.getX() + dx12 * t1), (int) (apPosition.getY() + dy12 * t1));
                    }
                }
            }
        }

        return null;
    }

    /**
     * Check if all the points on the outer walls are receiving a power >
     * threshold
     *
     * @param accessPoint
     * @param obstacles
     * @return true in case all the points are covered, otherwise return false
     */
    public static Boolean checkIfPointIsOptimal(AccessPoint accessPoint, ArrayList<Obstacle> obstacles) {
        final double minThresh = Constants.isLaptop ? PathLossModels.MIN_THRESHOLDLAPTOP : PathLossModels.MIN_THRESHOLDMOBILE;

        double power = 0;
        if (!obstacles.isEmpty() && accessPoint != null) {

            try {
                // Iterating on each outer wall
                for (Obstacle segment : obstacles) {
                    if (segment.isOuterWall()) {
                        // Calculating the step of this segment to scan all its points
                        double step = 1 / (segmentLength(segment.getStartPoint(), segment.getEndPoint(), false));
                        
                        // dx and dy are used to calculate the coordinates of each point
                        double dx = segment.getEndPoint().getX() - segment.getStartPoint().getX(), dy = segment.getEndPoint().getY() - segment.getStartPoint().getY();

                        // Iterating on each point to check if minimum power is received
                        for (double index = 0; index <= 1; index += step) {
                            int x = (int) (segment.getStartPoint().getX() + dx * index);
                            int y = (int) (segment.getStartPoint().getY() + dy * index);

                            power = accessPoint.getModel().getEmissionPower()
                                    - PathLossModels.solahsModelModified(new Point(x, y),
                                            accessPoint,
                                            obstacles,
                                            segmentLength(segment.getStartPoint(), segment.getEndPoint(), true))
                                    + segment.getMaterial().getAttenuationPerCM();
                            
                            if (power < minThresh) {
                                //System.out.println("Power" + power);
                                return false;
                            }
                        }
                    }

                }
            } catch (ConcurrentModificationException ex) {
                System.out.println("\n\nException : " + ex + "\n\n");
            }
            System.out.println("Optimal" + power);
            return true;
        }
        return false;

    }

    public static double segmentLength(Point P1, Point P2, boolean isCentiMeters) {
        return isCentiMeters ? Math.sqrt((P1.x - P2.x) * (P1.x - P2.x) + (P1.y - P2.y) * (P1.y - P2.y)) * Constants.DRAWING_SCALE / Constants.DEFAULT_GRID_SIZE
                : Math.sqrt((P1.x - P2.x) * (P1.x - P2.x) + (P1.y - P2.y) * (P1.y - P2.y));
    }

    public static Point movePointToPolygon(Point startPoint, Point intersectionPoint) {
        float dx = intersectionPoint.x - startPoint.x;
        float dy = intersectionPoint.y - startPoint.y;

        int x = (int) (startPoint.x + dx * 0.90);
        int y = (int) (startPoint.y + dy * 0.90);

        return new Point(x, y);
    }

    public static Point reverseSolah(Point receivingPoint, Point endPoint, ArrayList<Obstacle> obstacles, AccessPoint ap) {

        double attenuation = ap.getModel().getEmissionPower() - PathLossModels.MIN_THRESHOLDLAPTOP;

        for (Obstacle obs : obstacles) {
            if (!obs.isOuterWall() && PathLossModels.doSegmentsIntersect(receivingPoint, endPoint, obs)) {
                attenuation -= obs.getMaterial().getAttenuationPerCM();
            }
        }
        attenuation -= ap.getModel().getPathLoss1m();
        //System.out.println("ReverseSolah atten" + attenuation);

        double distToApInCm = (Math.pow(10, attenuation / ((double) 10 * PathLossModels.NLOS_EXPONENT))) * 100;
        //System.out.println("ReverseSolah distToApCm" + distToApInCm);
        double distToApInPx = distToApInCm * ((double) Constants.DEFAULT_GRID_SIZE / Constants.DEFAULT_DRAWING_SCALE);

        //System.out.println("ReverseSolah distToAp" + distToApInPx);
        float dx = endPoint.x - receivingPoint.x;
        float dy = endPoint.y - receivingPoint.y;
        double t1 = distToApInPx / Math.sqrt((endPoint.x - receivingPoint.x) * (endPoint.x - receivingPoint.x)
                + (endPoint.y - receivingPoint.y) * (endPoint.y - receivingPoint.y));

        //System.out.println("ReverseSolah dx" + dx);
        //System.out.println("ReverseSolah dy" + dy);
        //System.out.println("ReverseSolah t1" + t1);
        int x = (int) (receivingPoint.x + dx * t1);
        int y = (int) (receivingPoint.y + dy * t1);

        return new Point(x, y);
    }
    
    public static double getAverageReceivedPower(AccessPoint accessPoint, ArrayList<Obstacle> obstacles, BruteForceCalculation bfc){
        final double minThresh = Constants.isLaptop ? PathLossModels.MIN_THRESHOLDLAPTOP : PathLossModels.MIN_THRESHOLDMOBILE;
        
        if (!obstacles.isEmpty() && accessPoint != null){
            
            double powerSum = 0, receivingPoints = 0, power;
            double dy, dx, step;
            
            int x,y;
            
            for(Obstacle obstacle : obstacles){
                if(obstacle.isOuterWall()){
                    step = 1 / (segmentLength(obstacle.getStartPoint(), obstacle.getEndPoint(), false));
                    dx = obstacle.getEndPoint().getX() - obstacle.getStartPoint().getX();
                    dy = obstacle.getEndPoint().getY() - obstacle.getStartPoint().getY();
                    
                    for (double index = 0; index <= 1; index += step) {
                            x = (int) (obstacle.getStartPoint().getX() + dx * index);
                            y = (int) (obstacle.getStartPoint().getY() + dy * index);

                            power = accessPoint.getModel().getEmissionPower()
                                    - PathLossModels.solahsModelModified(new Point(x, y),
                                            accessPoint,
                                            obstacles,
                                            segmentLength(obstacle.getStartPoint(), obstacle.getEndPoint(), true))
                                    + obstacle.getMaterial().getAttenuationPerCM();
                            
                            if(power < minThresh){
                                bfc.setIsLocationOptimal(false);
                            }
                            
                            powerSum += power;
                            receivingPoints ++;
                        }
                }
            }
            if(powerSum != 0 && receivingPoints != 0)
                return powerSum / receivingPoints;
        }
        return 0;
    }
    
    public static Point centroid(ArrayList<Obstacle> outerWalls) {
        if (!outerWalls.isEmpty()) {
            int centroidX = 0, centroidY = 0;

            for (Obstacle obs : outerWalls) {
                centroidX += obs.getStartPoint().getX() + obs.getEndPoint().getX();
                centroidY += obs.getStartPoint().getY() + obs.getEndPoint().getY();
            }
            return new Point(centroidX / (outerWalls.size() * 2), centroidY / (outerWalls.size() * 2));
        }
        
        else {
            return new Point(960, 540);
        }
    }
    
    public static boolean isPointOverOuterWall(Point point, ArrayList<Obstacle> obstacles) {
            // See if point is over the outerWall.
        for(Obstacle obstacle : obstacles){
            if (findDistanceToSegmentSquared(point, obstacle.getStartPoint(),
                    obstacle.getEndPoint()) < 25) {
                return true;
            }
        }
        return false;
    }
    
    private static double findDistanceToSegmentSquared(Point pt, Point p1, Point p2) {
        float dx = p2.x - p1.x;
        float dy = p2.y - p1.y;
        if ((dx == 0) && (dy == 0)) {
            // It's a point not a line segment.
            dx = pt.x - p1.x;
            dy = pt.y - p1.y;
            return dx * dx + dy * dy;
        }

        // Calculate the t that minimizes the distance.
        float t = ((pt.x - p1.x) * dx + (pt.y - p1.y) * dy) / (dx * dx + dy * dy);

        // See if this represents one of the segment's
        // end points or a point in the middle.
        if (t < 0) {
            dx = pt.x - p1.x;
            dy = pt.y - p1.y;
        } else if (t > 1) {
            dx = pt.x - p2.x;
            dy = pt.y - p2.y;
        } else {
            dx = pt.x - (p1.x + t * dx);
            dy = pt.y - (p1.y + t * dy);
        }

        return dx * dx + dy * dy;
    }
}

package Utilities;

import Entities.Obstacle;
import Entities.AccessPoint;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ALI
 */
public class PathLossModels {

    public static final int LOG_NORMAL_EXPONENT = 3;

    public static final double DEFAULT_NLOS_EXPONENT = 2.52;
    public static final double DEFAULT_LOS_EXPONENT = 1.04;
    public static final int DEFAULT_BREAK_POINT_DISTANCE = 400;
    public static final double DEFAULT_MIN_THRESHOLDLAPTOP = -90;
    public static final double DEFAULT_MIN_THRESHOLDMOBILE = -75;

    public static double NLOS_EXPONENT = 2;
    public static double LOS_EXPONENT = 1;
    public static int BREAK_POINT_DISTANCE = 800;
    public static double MIN_THRESHOLDLAPTOP = -90;
    public static double MIN_THRESHOLDMOBILE = -75;
    //public static final double MAX_THRESHOLD = -30;

    public static double solahsModel(Point receivingPoint, AccessPoint ap, ArrayList<Obstacle> obstacles, double distToAP) {
        double attenuation = 0;
        for (Obstacle obs : obstacles) {
            if (doSegmentsIntersect(ap.getLocation(), receivingPoint, obs)) {
                attenuation += obs.getMaterial().getAttenuationPerCM();
            }
        }
        attenuation += ap.getModel().getPathLoss1m();
        attenuation += distToAP < BREAK_POINT_DISTANCE ? 10 * LOS_EXPONENT * Math.log10(distToAP) : 10 * NLOS_EXPONENT * Math.log10(distToAP);
        return attenuation;
    }

    public static double solahsModelModified(Point receivingPoint, AccessPoint ap, ArrayList<Obstacle> obstacles, double distToAP) {
        double attenuation = 0;

        for (Obstacle obs : obstacles) {
            if (doSegmentsIntersect(ap.getLocation(), receivingPoint, obs)) {
                attenuation += obs.getMaterial().getAttenuationPerCM();
            }
        }

        //if(distToAP > 100){
        attenuation += ap.getModel().getPathLoss1m();
        attenuation += (10 * LOS_EXPONENT * Math.log10(distToAP / 100))
                + (10 * (NLOS_EXPONENT - LOG_NORMAL_EXPONENT) * Math.log10(1 + distToAP / BREAK_POINT_DISTANCE));
        attenuation += new Random().nextGaussian() * 2;
        //}

        return attenuation;
    }

    public static double logNormalModel(Point receivingPoint, AccessPoint ap, ArrayList<Obstacle> obstacles, double distToAP) {
        double attenuation = 0;
        for (Obstacle obs : obstacles) {
            if (doSegmentsIntersect(ap.getLocation(), receivingPoint, obs)) {
                attenuation += obs.getMaterial().getAttenuationPerCM();
            }
        }

        attenuation += 10 * LOG_NORMAL_EXPONENT * Math.log10(distToAP / ap.getModel().getPathLoss1m()) + new Random().nextGaussian() * 2 + 2;

        return attenuation;
    }

    public static boolean doSegmentsIntersect(Point start, Point end, Obstacle obs) {
        // Get the segments' parameters.
        float dx12 = end.x - start.x;
        float dy12 = end.y - start.y;
        float dx34 = obs.getEndPoint().x - obs.getStartPoint().x;
        float dy34 = obs.getEndPoint().y - obs.getStartPoint().y;

        // Solve for t1 and t2
        float denominator = (dy12 * dx34 - dx12 * dy34);

        if (denominator != 0) {
            float t1
                    = ((start.x - obs.getStartPoint().x) * dy34 + (obs.getStartPoint().y - start.y) * dx34) / denominator;

            float t2
                    = ((obs.getStartPoint().x - start.x) * dy12 + (start.y - obs.getStartPoint().y) * dx12) / -denominator;

            return ((t1 >= 0) && (t1 <= 1) && (t2 >= 0) && (t2 <= 1));
        }

        return false;
    }
    
    public static float getSegmentsIntersection(Point start, Point end, Obstacle obs) {
        // Get the segments' parameters.
        float dx12 = end.x - start.x;
        float dy12 = end.y - start.y;
        float dx34 = obs.getEndPoint().x - obs.getStartPoint().x;
        float dy34 = obs.getEndPoint().y - obs.getStartPoint().y;

        // Solve for t1 and t2
        float denominator = (dy12 * dx34 - dx12 * dy34);

        if (denominator != 0) {
            float t1
                    = ((start.x - obs.getStartPoint().x) * dy34 + (obs.getStartPoint().y - start.y) * dx34) / denominator;

            float t2
                    = ((obs.getStartPoint().x - start.x) * dy12 + (start.y - obs.getStartPoint().y) * dx12) / -denominator;

            return ((t1 >= 0) && (t1 <= 1) && (t2 >= 0) && (t2 <= 1)) ? t1 : -10;
        }

        return -10;
    }

    public static double normalizeValue(double value, double maxThreshold) {
        double MIN_THRESHOLD = Constants.isLaptop ? MIN_THRESHOLDLAPTOP : MIN_THRESHOLDMOBILE;
        return (value - MIN_THRESHOLD) / (maxThreshold - MIN_THRESHOLD);
    }
}

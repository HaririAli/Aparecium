package Entities;


import java.awt.Point;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ALI
 */
public class Obstacle implements java.io.Serializable {
    
    private boolean outerWall = false;
    private boolean innerWall = false;
    private Point startPoint, endPoint;
    private Material material;
    
    public Obstacle(Point startPoint, Point endPoint, Material material){
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.material = material;
    }
    
    public Point getStartPoint() {
        return startPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean isOuterWall() {
        return outerWall;
    }

    public boolean isInnerWall() {
        return innerWall;
    }
    
    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    public void setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setOuterWall(boolean outerWall) {
        this.outerWall = outerWall;
    }

    public void setInnerWall(boolean innerWall) {
        this.innerWall = innerWall;
    }
    
    
    
}

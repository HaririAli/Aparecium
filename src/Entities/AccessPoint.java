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
public class AccessPoint implements java.io.Serializable {
    
    private String name;
    private Point location;
    private AccessPointModel model;
    
    public AccessPoint(Point location){
        this.location = location;
    }
    
    public AccessPoint(Point location, String name, AccessPointModel model){
        this.location = location;
        this.name = name;
        this.model = model;
    }

    public AccessPointModel getModel() {
        return model;
    }

    public String getName() {
        return name;
    }

    public Point getLocation() {
        return location;
    }

    public void setModel(AccessPointModel model) {
        this.model = model;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(Point location) {
        this.location = location;
    }
}
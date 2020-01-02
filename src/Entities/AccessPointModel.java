package Entities;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ALI
 */
public class AccessPointModel implements java.io.Serializable {
    
    private int id;
    private String model, brand;
    private float emissionPower; //in dBm
    private float pathLoss1m;
    
    public AccessPointModel(int id, String model, String brand, float emissionPower, float pathLoss1m){
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.emissionPower = emissionPower;
        this.pathLoss1m = pathLoss1m;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public float getEmissionPower() {
        return emissionPower;
    }

    public void setEmissionPower(float emissionPower) {
        this.emissionPower = emissionPower;
    }

    public float getPathLoss1m() {
        return pathLoss1m;
    }

    public void setPathLoss1m(float pathLoss1m) {
        this.pathLoss1m = pathLoss1m;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return brand + " " + model;
    }

}

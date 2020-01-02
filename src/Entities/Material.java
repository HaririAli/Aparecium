package Entities;

import java.awt.Color;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ALI
 */
public class Material implements java.io.Serializable {
    
    private int id;
    private String name;
    private float attenuationPerCM;
    private Color color;
    
    public Material(int id, String name, float attenuationPerCM, Color color){
        this.id = id;
        this.name = name;
        this.attenuationPerCM = attenuationPerCM;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getAttenuationPerCM() {
        return attenuationPerCM;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAttenuationPerCM(float attenuationPerCM) {
        this.attenuationPerCM = attenuationPerCM;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
    
    @Override
    public String toString(){
        return this.name + "  " + attenuationPerCM + " dBm";
    }
    
}

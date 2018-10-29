package mta.com.final_project.model;

import com.google.gson.Gson;

import java.util.Date;

import mta.com.final_project.LatLng;

public class SearchParams {

   private LatLng selectPlace;
   private Integer radius ;
   private Date fromDate ;
   private String  animalType;
   private  Boolean isLost;
    public Date getFromDate() {
        return fromDate;
    }

    public Integer getRadius() {
        return radius;
    }

    public LatLng getSelectPlace() {
        return selectPlace;
    }

    public String getAnimalType() {
        return animalType;
    }

    public void setAnimalType(String animalType) {
        this.animalType = animalType;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setSelectPlace(LatLng selectPlace) {
        this.selectPlace = selectPlace;
    }



    public Boolean getLost() {
        return isLost;
    }

    public void setLost(Boolean lost) {
        isLost = lost;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}


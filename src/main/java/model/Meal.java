package model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Meal {

    private final String id;
    private String name;

    private Long carbs;
    private Long protein;
    private Long fat;
    private Long kcal;

    public Meal(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCarbs() {
        return carbs;
    }

    public void setCarbs(Long carbs) {
        this.carbs = carbs;
    }

    public Long getProtein() {
        return protein;
    }

    public void setProtein(Long protein) {
        this.protein = protein;
    }

    public Long getFat() {
        return fat;
    }

    public void setFat(Long fat) {
        this.fat = fat;
    }

    public Long getKcal() {
        return kcal;
    }

    public void setKcal(Long kcal) {
        this.kcal = kcal;
    }

    @Override
    public String toString() {
        return id + "," +
                name + "," +
                carbs + "," +
                protein + "," +
                fat + "," +
                kcal;
    }
}

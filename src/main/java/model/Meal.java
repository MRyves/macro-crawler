package model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Meal {

    private final String id;
    private String name;

    private Long carbs;
    private Long protein;
    private Long fat;

    private boolean healthy;

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
        long carbKcal = this.carbs == null ? 0 : this.carbs * 4;
        long fatKcal = this.fat == null ? 0 : this.fat * 9;
        long proteinKcal = this.protein == null ? 0 : this.protein * 4;
        return carbKcal + fatKcal + proteinKcal;
    }


    @Override
    public String toString() {
        return "Meal{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", carbs=" + carbs +
                ", protein=" + protein +
                ", fat=" + fat +
                ", kcal=" + getKcal() +
                ", isHealthy=" + healthy +
                '}';
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Meal meal = (Meal) o;

        return new EqualsBuilder()
                .append(healthy, meal.healthy)
                .append(id, meal.id)
                .append(name, meal.name)
                .append(carbs, meal.carbs)
                .append(protein, meal.protein)
                .append(fat, meal.fat)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(carbs)
                .append(protein)
                .append(fat)
                .append(healthy)
                .toHashCode();
    }
}

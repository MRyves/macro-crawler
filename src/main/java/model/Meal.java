package model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Meal {

    private final String id;
    private String name;

    private long carbs;
    private long protein;
    private long fat;

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

    public long getCarbs() {
        return carbs;
    }

    public void setCarbs(long carbs) {
        this.carbs = carbs;
    }

    public long getProtein() {
        return protein;
    }

    public void setProtein(long protein) {
        this.protein = protein;
    }

    public long getFat() {
        return fat;
    }

    public void setFat(long fat) {
        this.fat = fat;
    }

    public Long getKcal() {
        long carbKcal = this.carbs * 4;
        long fatKcal = this.fat * 9;
        long proteinKcal = this.protein * 4;
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

        return new EqualsBuilder().append(id, meal.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }
}

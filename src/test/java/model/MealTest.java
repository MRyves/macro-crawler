package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MealTest {
    @Test
    void testKcalCalculation_noProtein() {
        Meal m = new Meal("unit-test-id");
        m.setFat(100L);
        m.setCarbs(100L);

        assertEquals(400 + 900, m.getKcal());
    }
}

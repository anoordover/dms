package com.amplexor.ia.utils;


import java.time.LocalDate;
import java.util.Random;

/**
 * Created by minkenbergs on 28-9-2016.
 */
public class RandomGenerator {
    private Random objRandom;

    public RandomGenerator() {
        objRandom = new Random(System.currentTimeMillis());
    }

    public String generateId(int iNumCharacters) {
        if (iNumCharacters == 1) {
            return String.format("%d", objRandom.nextInt(9));
        } else {
            int iValueMin = (int) Math.pow(10.0d, (double) iNumCharacters);
            int iValueMax = (int) Math.pow(10.0d, (double) (iNumCharacters + 1)) - 1;
            return String.format("%d", iValueMin + objRandom.nextInt(iValueMax - iValueMin));
        }
    }

    public LocalDate generateVerzendDag() {
        int minDay = (int) LocalDate.of(2015, 1, 1).toEpochDay();
        int maxDay = (int) LocalDate.now().toEpochDay();
        long randomDay = minDay + objRandom.nextInt(maxDay - minDay);
        return LocalDate.ofEpochDay(randomDay);
    }

    public int randomInt(int iMax) {
        return objRandom.nextInt(iMax);
    }
}

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
            long lValueMin = (long) Math.pow(10.0d, (double) iNumCharacters - 1);
            long lValueMax = (long) Math.pow(10.0d, (double) (iNumCharacters)) - 1;
            long lValueDiff = lValueMax - lValueMin;
            long lGenerated = Math.abs(objRandom.nextLong() % lValueDiff);
            return String.format("%d", lValueMin + lGenerated);
        }
    }

    public LocalDate generateVerzendDag() {
        int minDay = (int) LocalDate.of(2015, 1, 1).toEpochDay();
        int maxDay = (int) LocalDate.now().toEpochDay();
        long randomDay = minDay + objRandom.nextInt(maxDay - minDay);
        return LocalDate.ofEpochDay(randomDay);
    }

    public int randomInt(int iMax) {
        if(iMax == 0) {
            return 0;
        }

        return objRandom.nextInt(iMax);
    }
}

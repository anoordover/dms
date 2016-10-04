package com.amplexor.ia.utils;


import java.time.LocalDate;
import java.util.Date;
import java.util.Random;

/**
 * Created by minkenbergs on 28-9-2016.
 */
public class RandomGenerator {
    private Random r ;

    public RandomGenerator() {
        r = new Random();
    }

    public long generateArchiefDocumentId(){
        long LOWER_RANGE = 1000000000;
        long UPPER_RANGE = 1999999999;
        return LOWER_RANGE + (long)(r.nextDouble()*(UPPER_RANGE - LOWER_RANGE));
    }

    public long generateArchiefPersoonNummer(){
        long LOWER_RANGE = 1000000000;
        long UPPER_RANGE = 1999999999;
        return LOWER_RANGE + (long)(r.nextDouble()*(UPPER_RANGE - LOWER_RANGE));
    }

    public long generatePersoonBurgersservicenummer(){
        long LOWER_RANGE = 100000000;
        long UPPER_RANGE = 199999999;
        return LOWER_RANGE + (long)(r.nextDouble()*(UPPER_RANGE - LOWER_RANGE));
    }

    public long generateDocumentKenmerkNr(){
        long LOWER_RANGE = 10000;
        long UPPER_RANGE = 99999;
        return LOWER_RANGE + (long)(r.nextDouble()*(UPPER_RANGE - LOWER_RANGE));
    }

    public LocalDate generateVerzendDag(){
        int minDay = (int) LocalDate.of(2015, 1, 1).toEpochDay();
        int maxDay = (int) LocalDate.now().toEpochDay();
        long randomDay = minDay + r.nextInt(maxDay - minDay);
        return LocalDate.ofEpochDay(randomDay);
    }

    public int generateIntOneToSix(){
        return r.nextInt(6);
    }

    public <T extends Enum<?>> T generateRandomEnum(Class<T> enumClass){
        int x = r.nextInt(enumClass.getEnumConstants().length);
        return enumClass.getEnumConstants()[x];
    }
}

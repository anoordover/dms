package com.amplexor.ia;

import com.amplexor.ia.enums.ArchiefDocumenttitel;
import com.amplexor.ia.utils.RandomGenerator;

/**
 * Created by minkenbergs on 28-9-2016.
 */
public class MetaDataGenerator {

    public static void main(String[] cArgs) {
        RandomGenerator rg = new RandomGenerator();

        for(int i = 0; i < 10 ; i++){
            System.out.println(rg.generateRandomEnum(ArchiefDocumenttitel.class));
        }
    }

}

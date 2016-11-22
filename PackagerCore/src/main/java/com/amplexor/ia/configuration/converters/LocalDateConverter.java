package com.amplexor.ia.configuration.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.time.LocalDate;

/**
 * Created by admjzimmermann on 5-10-2016.
 */
public class LocalDateConverter implements Converter {
    @Override
    public void marshal(Object objValue, HierarchicalStreamWriter objWriter, MarshallingContext objContext) {
        final LocalDate objDate = (LocalDate)objValue;
        objWriter.setValue(String.format("%04d-%02d-%02d", objDate.getYear(), objDate.getMonthValue(), objDate.getDayOfMonth()));
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader objReader, UnmarshallingContext objContext) {
        int iYear = 0;
        int iMonth = 0;
        int iDay = 0;
        while(objReader.hasMoreChildren()) {
            objReader.moveDown();
            String[] sDateParts = objReader.getValue().split("-");
            if(sDateParts.length == 3) {
                iYear = Integer.parseInt(sDateParts[0]);
                iMonth = Integer.parseInt(sDateParts[1]);
                iDay = Integer.parseInt(sDateParts[2]);
            }
            objReader.moveUp();
        }
        return LocalDate.of(iYear, iMonth, iDay);
    }

    @Override
    public boolean canConvert(Class aClass) {
        return aClass.equals(LocalDate.class);
    }
}

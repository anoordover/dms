package com.amplexor.ia.configuration.converters;

import com.amplexor.ia.exception.ExceptionHelper;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;

import java.util.AbstractMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * Custom XStream Converter class for reading a number of XML Elements and converting them to a {@link Map<String, Object>}
 * Created by admjzimmermann on 6-9-2016.
 */
public class ParameterConverter implements Converter {
    @Override
    public void marshal(Object objValue, HierarchicalStreamWriter objWriter, MarshallingContext objContext) {
        final AbstractMap<String, Object> cMap = (AbstractMap<String, Object>) objValue;
        for (Map.Entry<String, Object> entry : cMap.entrySet()) {
            objWriter.startNode(entry.getKey());
            objWriter.setValue(entry.getValue().toString());
            objWriter.endNode();
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader objReader, UnmarshallingContext objContext) {
        Map<String, Object> cReturn = new TreeMap<>();
        while (objReader.hasMoreChildren()) {
            objReader.moveDown();
            cReturn.put(objReader.getNodeName(), objReader.getValue());
            objReader.moveUp();
        }
        return cReturn;
    }

    @Override
    public boolean canConvert(Class objClass) {
        return AbstractMap.class.isAssignableFrom(objClass);
    }
}

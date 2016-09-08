package com.amplexor.ia.configuration.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.*;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class ParameterConverter implements Converter {
    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        final AbstractMap<String, Object> map = (AbstractMap<String, Object>) value;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            writer.startNode(entry.getKey());
            writer.setValue(entry.getValue().toString());
            writer.endNode();
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Map<String, Object> map = new TreeMap<String, Object>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            map.put(reader.getNodeName(), reader.getValue());
            reader.moveUp();
        }
        return map;
    }

    @Override
    public boolean canConvert(Class aClass) {
        return AbstractMap.class.isAssignableFrom(aClass);
    }
}

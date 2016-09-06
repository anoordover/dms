package com.amplexor.ia.configuration;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admjzimmermann on 6-9-2016.
 */
public class ParameterConverter implements Converter {
    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        final AbstractMap<String, String> map = (AbstractMap<String, String>) value;
        for (Map.Entry<String, String> entry : map.entrySet()) {

            writer.startNode(entry.getKey());
            writer.setValue(entry.getValue().toString());
            writer.endNode();

        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Map<String, String> map = new HashMap<String, String>();
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

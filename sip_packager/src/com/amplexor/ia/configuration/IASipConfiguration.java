package com.amplexor.ia.configuration;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by admjzimmermann on 15-9-2016.
 */
public class IASipConfiguration {
    @XStreamAlias("holding_name")
    private String msHoldingName;

    @XStreamAlias("application_name")
    private String msApplicationName;

    @XStreamAlias("producer_name")
    private String msProducerName;

    @XStreamAlias("entity_name")
    private String msEntityName;

    @XStreamAlias("schema_declaration")
    private String msSchemaDeclaration;

    public String getHoldingName() {
        return msHoldingName;
    }

    public String getApplicationName() {
        return msApplicationName;
    }

    public String getProducerName() {
        return msProducerName;
    }

    public String getEntityName() {
        return msEntityName;
    }

    public String getSchemaDeclaration() {
        return msSchemaDeclaration;
    }
}

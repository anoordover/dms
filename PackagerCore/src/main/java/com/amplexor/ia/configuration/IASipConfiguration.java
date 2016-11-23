package com.amplexor.ia.configuration;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * POJO for holding configuration pertaining to the creation of SIP Files
 * Created by admjzimmermann on 15-9-2016.
 */
public class IASipConfiguration extends PluggableObjectConfiguration {
    @XStreamAlias("holding_name")
    private String msHoldingName;

    @XStreamAlias("application_name")
    private String msApplicationName;

    @XStreamAlias("producer_name")
    private String msProducerName;

    @XStreamAlias("entity_name")
    private String msEntityName;

    @XStreamAlias("document_element_name")
    private String msDocumentElementName;

    @XStreamAlias("schema_declaration")
    private String msSchemaDeclaration;

    @XStreamAlias("sip_output_directory")
    private String msSipOutputDirectory;

    @XStreamAlias("sip_backup_directory")
    private String msSipBackupDirectory;


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

    public String getDocumentElementName() {
        return msDocumentElementName;
    }

    public String getSipOutputDirectory() {
        return msSipOutputDirectory;
    }

    public String getSipBackupDirectory() {
        return msSipBackupDirectory;
    }
}

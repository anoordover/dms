package com.amplexor.ia.configuration;

import com.amplexor.ia.exception.AMPError;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

/**
 * Created by zimmermannj on 9/23/2016.
 */
public class ExceptionConfiguration {
    @XStreamImplicit(itemFieldName = "error")
    private List<AMPError> mcErrors;


    public ExceptionConfiguration() {

    }

    public AMPError getError(int iCode) {
        for (AMPError objError : mcErrors) {
            if (objError.getErrorCode() == iCode) {
                return objError;
            }
        }

        return new AMPError(iCode, "The following exception was thrown", "log_error");
    }
}

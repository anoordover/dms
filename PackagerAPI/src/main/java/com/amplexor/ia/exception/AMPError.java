package com.amplexor.ia.exception;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * POJO for holding information about SIP Packager specific errors
 * Created by zimmermannj on 9/23/2016.
 */
public class AMPError {
    @XStreamAlias("error_code")
    private int miErrorCode;

    @XStreamAlias("error_text")
    private String msErrorText;

    @XStreamAlias("error_handling")
    private String msErrorHandling;

    public AMPError() {

    }

    public AMPError(int iCode, String sErrorText, String sErrorHandling) {
        miErrorCode = iCode;
        msErrorText = sErrorText;
        msErrorHandling = sErrorHandling;
    }

    public int getErrorCode() {
        return miErrorCode;
    }

    public String getErrorText() {
        return msErrorText;
    }

    public String getErrorHandling() {
        return msErrorHandling;
    }
}

package nl.hetcak.dms.ia.web.exceptions;

import java.security.PrivilegedActionException;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class InfoArchiveResponseException extends RequestResponseException {
    public static final String ERROR_MESSAGE = "InfoArchive responded with a error, please contact an Administrator.";
    private static final String ERROR_TITLE_TIME_OUT = "SEARCH_TIMEOUT";
    private static final int ERROR_CODE_TIME_OUT_SINGLE_DOC = 2001;
    private static final int ERROR_CODE_TIME_OUT_LIST_DOC = 2002;

    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param errorCode        The error code.
     * @param userErrorMessage The error description that should be returned to the user.
     */
    public InfoArchiveResponseException(int errorCode, String userErrorMessage) {
        super(errorCode, userErrorMessage);
    }

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message          the detail message. The detail message is saved for
     *                         later retrieval by the {@link #getMessage()} method.
     * @param errorCode        The error code.
     * @param userErrorMessage The error description that should be returned to the user.
     */
    public InfoArchiveResponseException(String message, int errorCode, String userErrorMessage) {
        super(message, errorCode, userErrorMessage);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this exception's detail message.
     *
     * @param message          the detail message (which is saved for later retrieval
     *                         by the {@link #getMessage()} method).
     * @param cause            the cause (which is saved for later retrieval by the
     *                         {@link #getCause()} method).  (A <tt>null</tt> value is
     *                         permitted, and indicates that the cause is nonexistent or
     *                         unknown.)
     * @param errorCode        The error code.
     * @param userErrorMessage The error description that should be returned to the user.
     * @since 1.4
     */
    public InfoArchiveResponseException(String message, Throwable cause, int errorCode, String userErrorMessage) {
        super(message, cause, errorCode, userErrorMessage);
    }

    /**
     * Constructs a new exception with the specified cause and a detail
     * message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * This constructor is useful for exceptions that are little more than
     * wrappers for other throwables (for example, {@link
     * PrivilegedActionException}).
     *
     * @param cause            the cause (which is saved for later retrieval by the
     *                         {@link #getCause()} method).  (A <tt>null</tt> value is
     *                         permitted, and indicates that the cause is nonexistent or
     *                         unknown.)
     * @param errorCode        The error code.
     * @param userErrorMessage The error description that should be returned to the user.
     * @since 1.4
     */
    public InfoArchiveResponseException(Throwable cause, int errorCode, String userErrorMessage) {
        super(cause, errorCode, userErrorMessage);
    }

    /**
     * Constructs a new exception with the specified detail message,
     * cause, suppression enabled or disabled, and writable stack
     * trace enabled or disabled.
     *
     * @param message            the detail message.
     * @param cause              the cause.  (A {@code null} value is permitted,
     *                           and indicates that the cause is nonexistent or unknown.)
     * @param enableSuppression  whether or not suppression is enabled
     *                           or disabled
     * @param writableStackTrace whether or not the stack trace should
     *                           be writable
     * @param errorCode          The error code.
     * @param userErrorMessage   The error description that should be returned to the user.
     * @since 1.7
     */
    public InfoArchiveResponseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int errorCode, String userErrorMessage) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode, userErrorMessage);
    }

    public static int defineErrorCode(String errorTitle, Boolean expectedList) {
        if (errorTitle.contains(ERROR_TITLE_TIME_OUT)) {
            if (expectedList) {
                return ERROR_CODE_TIME_OUT_LIST_DOC;
            } else {
                return ERROR_CODE_TIME_OUT_SINGLE_DOC;
            }
        }
        return 0;
    }
}

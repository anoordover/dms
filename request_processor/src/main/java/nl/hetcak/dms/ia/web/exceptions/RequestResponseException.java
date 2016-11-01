package nl.hetcak.dms.ia.web.exceptions;

import java.security.PrivilegedActionException;

/**
 * (c) 2016 AMPLEXOR International S.A., All rights reserved.
 *
 * @author Jeroen.Pelt@AMPLEXOR.com
 */
public class RequestResponseException extends Exception {
    private int errorCode = -1;
    private String userErrorMessage;

    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param errorCode        The error code.
     * @param userErrorMessage The error description that should be returned to the user.
     */
    public RequestResponseException(int errorCode, String userErrorMessage) {
        this.errorCode = errorCode;
        this.userErrorMessage = userErrorMessage;
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
    public RequestResponseException(String message, int errorCode, String userErrorMessage) {
        super(message);
        this.errorCode = errorCode;
        this.userErrorMessage = userErrorMessage;
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
    public RequestResponseException(String message, Throwable cause, int errorCode, String userErrorMessage) {
        super(message, cause);
        this.errorCode = errorCode;
        this.userErrorMessage = userErrorMessage;
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
    public RequestResponseException(Throwable cause, int errorCode, String userErrorMessage) {
        super(cause);
        this.errorCode = errorCode;
        this.userErrorMessage = userErrorMessage;
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
    public RequestResponseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int errorCode, String userErrorMessage) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
        this.userErrorMessage = userErrorMessage;
    }

    /**
     * Gets the error code.
     *
     * @return the error code.
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the error code.
     *
     * @param errorCode the error code.
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Gets the user error message.
     *
     * @return the user error message.
     */
    public String getUserErrorMessage() {
        return userErrorMessage;
    }

    /**
     * sets the user error message.
     *
     * @param userErrorMessage the user error message.
     */
    public void setUserErrorMessage(String userErrorMessage) {
        this.userErrorMessage = userErrorMessage;
    }
}

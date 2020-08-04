package org.ethelred.mc.pack

/**
 *
 */
class InvalidPackException extends Exception {
    InvalidPackException(String message) {
        super(message)
    }

    InvalidPackException(String s, Throwable cause) {
        super(s, cause)
    }
}


package com.sante.senegal.exceptions;

import java.util.HashMap;
import java.util.Map;

public class InscriptionException extends RuntimeException {
    public InscriptionException(String message) {
        super(message);
    }

    public InscriptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
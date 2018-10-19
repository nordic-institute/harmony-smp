package eu.europa.ec.edelivery.smp.ui.exception;

import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

public class UIException {


        private HttpStatus status;
        private String message;
        private List<String> errors;

        public UIException(HttpStatus status, String message, List<String> errors) {
            super();
            this.status = status;
            this.message = message;
            this.errors = errors;
        }

        public UIException(HttpStatus status, String message, String error) {
            super();
            this.status = status;
            this.message = message;
            errors = Arrays.asList(error);
        }
    }

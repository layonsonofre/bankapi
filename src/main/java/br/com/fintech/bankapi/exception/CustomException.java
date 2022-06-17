package br.com.fintech.bankapi.exception;


import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class CustomException extends RuntimeException {
    private HttpStatus status = null;
    private transient Object data = null;

    public CustomException() {
        super();
    }

    public CustomException(String message) {
        super(message);
    }

    public CustomException(HttpStatus httpStatus, String message) {
        this(message);
        this.status = httpStatus;
    }

    public CustomException(HttpStatus httpStatus, String message, Object data) {
        this(httpStatus, message);
        this.data = data;
    }
}

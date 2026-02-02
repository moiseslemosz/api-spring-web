package dio.web.api.handler;

import java.time.LocalDateTime;

public class ResponseError {
    private LocalDateTime timestamp = LocalDateTime.now();
    private String status = "ERROR";
    private int statusCode; 
    private String error;

    public ResponseError () {
}

    public ResponseError(String error, int statusCode) {
        this.error = error;
        this.statusCode = statusCode;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public int getStatusCode() {
        return statusCode;
    }
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
}
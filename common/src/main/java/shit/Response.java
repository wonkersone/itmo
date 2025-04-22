package shit;

import java.io.Serializable;

public class Response implements Serializable {

    public enum ResponseType{
        INFO, NEED_WORKER, ERROR, ONE_MORE_SCRIPT;
    }

    public ResponseType type;
    public String message;
    public boolean success;

    public Response() {}

    public Response(ResponseType type, String message) {
        this.type = type;
        this.message = message;
    }

    public Response(ResponseType type, boolean success, String message) {
        this.type = type;
        this.success = success;
        this.message = message;
    }

    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ResponseType getType() { return type; }
    public String getMessage() { return message; }
    public boolean isSuccess() { return success; }

    public void setType(ResponseType type) { this.type = type; }
    public void setMessage(String message) { this.message = message; }
    public void setSuccess(boolean success) { this.success = success; }

    @Override
    public String toString() {
        String newMessage = (message != null && message.length() > 30) ? message.substring(0, 30) + "..." : message;
        return "(type = " + type + ", success = " + success + ", message = " + newMessage + ")";
    }
}

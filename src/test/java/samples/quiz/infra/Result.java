package samples.quiz.infra;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

public class Result {

    public static Result ok() {
        return new Result("ok");
    }

    public static Result ok(String message, Object payload) {
        return new Result("ok", message, payload);
    }

    public static Result err(Throwable thr) {
        return new Result("err", thr.getMessage(), ExceptionUtils.getStackTrace(thr));
    }

    private String code;
    private String message;
    private Object payload;

    public Result() {
    }

    public Result(String code) {
        this.code = code;
    }

    public Result(String code, String message, Object payload) {
        this.code = code;
        this.message = message;
        this.payload = payload;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @JsonIgnore
    public String getMessageOrCode() {
        return message == null ? code : message;
    }

    public Object getPayload() {
        return payload;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Result [code=" + code + ", message=" + message + ", payloadClass="
                + ((payload != null) ? payload.getClass() : null) + ", payload=" + payload + "]";
    }
}

package exception;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{

    public enum ErrorCode{
        AlreadyTaken,
        Unauthorized,
        BadRequest,
        ServerError,
    }

    private ErrorCode code;

    public DataAccessException(ErrorCode code, String message){
        super(message);
        this.code = code;
    }

    public static DataAccessException fromResponse(int status, String json) {
        var map = new Gson().fromJson(json, HashMap.class);
        String message = map.get("message").toString();
        return new DataAccessException(fromHttpStatusCode(status), message);
    }

    public int toHttpStatusCode(){
        return switch(code) {
            case AlreadyTaken -> 403;
            case Unauthorized -> 401;
            case BadRequest -> 400;
            case ServerError -> 500;
        };
    }

    public static ErrorCode fromHttpStatusCode(int status){
        return switch(status){
            case 403 -> ErrorCode.AlreadyTaken;
            case 401 -> ErrorCode.Unauthorized;
            case 400 -> ErrorCode.BadRequest;
            case 500 -> ErrorCode.ServerError;
            default -> throw new IllegalStateException("Unexpected value: " + status);
        };
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage()));
    }

}

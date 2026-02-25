package dataaccess;

import com.google.gson.Gson;
import java.util.Map;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{

    public enum ErrorCode{
        AlreadyTaken,
        Unauthorized,
        BadRequest,
    }

    final private ErrorCode code;

    public DataAccessException(ErrorCode code, String message){
        super(message);
        this.code = code;
    }

    public int toHttpStatusCode(){
        return switch(code) {
            case AlreadyTaken -> 403;
            case Unauthorized -> 401;
            case BadRequest -> 400;
        };
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage()));
    }

}

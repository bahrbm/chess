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
        ServerError,
    }

    private ErrorCode code;

    public DataAccessException(ErrorCode code, String message){
        super(message);
        this.code = code;
    }

    public DataAccessException(String message, Exception ex){

    }

    public int toHttpStatusCode(){
        return switch(code) {
            case AlreadyTaken -> 403;
            case Unauthorized -> 401;
            case BadRequest -> 400;
            case ServerError -> 500;
        };
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage()));
    }

}

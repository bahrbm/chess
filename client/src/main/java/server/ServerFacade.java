package server;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.*;
import exception.DataAccessException;
import service.result.*;
import service.request.*;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public String register(RegisterRequest request) throws DataAccessException {
        var serverRequest = buildRequest("POST", "/user", request);
        var response = sendRequest(serverRequest);
        RegisterResult result = handleResponse(response, RegisterResult.class);
        return result.authToken();
    }

    public String login(LoginRequest r) throws DataAccessException {
        var serverRequest = buildRequest("POST","/session",r);
        var response = sendRequest(serverRequest);
        LoginResult result = handleResponse(response, LoginResult.class);
        return result.authToken();
    }

    public void logout(LogoutRequest r) throws DataAccessException{
        System.out.println("Sending request to server");
        var serverRequest = buildRequest("DELETE","/session",r);
        var response = sendRequest(serverRequest);
        handleResponse(response, LogoutResult.class);
    }

//    public JoinGameResult joinGame(JoinGameRequest r){
//        return null;
//    }

    private HttpRequest buildRequest(String method, String path, Object body, String... header) {

        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));

        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }

        if (header.length != 0){
            request.setHeader("Authorization", header[0]);
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws DataAccessException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new DataAccessException(DataAccessException.ErrorCode.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws DataAccessException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                System.out.print(body);
                throw DataAccessException.fromJson(body);
            }

            throw new DataAccessException(DataAccessException.ErrorCode.BadRequest, "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}

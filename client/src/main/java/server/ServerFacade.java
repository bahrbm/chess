package server;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.*;
import exception.DataAccessException;
import request.*;
import result.*;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    private String authToken = null;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public String register(RegisterRequest request) throws DataAccessException {
        var serverRequest = buildRequest("POST", "/user", request);
        var response = sendRequest(serverRequest);
        RegisterResult result = handleResponse(response, RegisterResult.class);
        authToken = result.authToken();
        return result.authToken();
    }

    public String login(LoginRequest r) throws DataAccessException {
        var serverRequest = buildRequest("POST","/session",r);
        var response = sendRequest(serverRequest);
        LoginResult result = handleResponse(response, LoginResult.class);
        authToken = result.authToken();
        return result.authToken();
    }

    public void logout(LogoutRequest r) throws DataAccessException{
        var serverRequest = buildRequest("DELETE","/session", r, r.authToken());
        var response = sendRequest(serverRequest);
        handleResponse(response, LogoutResult.class);
    }

    public void createGame(CreateGameRequest r) throws DataAccessException{
        var serverRequest = buildRequest("POST","/game", r, authToken);
        var response = sendRequest(serverRequest);
        handleResponse(response, CreateGameResult.class);
    }

    public ListGamesResult listGames(ListGamesRequest r) throws DataAccessException{
        var serverRequest = buildRequest("GET","/game", r, authToken);
        var response = sendRequest(serverRequest);
        return handleResponse(response, ListGamesResult.class);
    }

    public void joinGame(JoinGameRequest r) throws DataAccessException {
        var serverRequest = buildRequest("PUT","/game", r, authToken);
        var response = sendRequest(serverRequest);
        handleResponse(response, JoinGameResult.class);
    }

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
                throw DataAccessException.fromResponse(status, body);
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

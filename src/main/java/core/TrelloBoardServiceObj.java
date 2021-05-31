package core;

import beans.TrelloBoardCompact;
import beans.TrelloBoardLabel;
import beans.TrelloNewBoard;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import constants.EndPoints;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import utils.ReadProperties;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.lessThan;

public class TrelloBoardServiceObj {


    public static final URI BOARDS_URL = URI.create(EndPoints.URL);
    private Method requestMethod;

    //BEGINNING OF BUILDER PATTERN
    private Map<String, String> parameters;
    private Map<String, String> path;

    private TrelloBoardServiceObj(Map<String, String> parameters, Method method, Map<String, String> path) {
        this.parameters = parameters;
        this.path = path;
        this.requestMethod = method;
    }

    public static ApiRequestBuilder requestBuilder() {
        return new ApiRequestBuilder();
    }

    public static class ApiRequestBuilder {
        private Map<String, String> parameters = new HashMap<>();
        private Map<String, String> path = new LinkedHashMap<>();
        private Method requestMethod = Method.GET;


        public ApiRequestBuilder setMethod(Method method) {
            requestMethod = method;
            return this;
        }

        public ApiRequestBuilder propsAuthorization() {
            ReadProperties.main();
            parameters.put("key", ReadProperties.props.get("key"));
            parameters.put("token", ReadProperties.props.get("token"));
            return this;
        }

        public ApiRequestBuilder setKey(String key) {
            parameters.put("key", key);
            return this;
        }

        public ApiRequestBuilder setToken(String token) {
            parameters.put("token", token);
            return this;
        }

        public ApiRequestBuilder setName(String name) {
            parameters.put("name", name);
            return this;
        }

        public ApiRequestBuilder setLabelColor(String color) {
            parameters.put("color", color);
            return this;
        }

        public ApiRequestBuilder setFields(String[] fields) {

            parameters.put("fields", Arrays.stream(fields).collect(Collectors.joining(",")));
            return this;
        }

        public ApiRequestBuilder setID(String id) {
            path.put("board_id", id);
            return this;
        }

        public ApiRequestBuilder setType(String type) {
            parameters.put("type", type);
            return this;
        }

        public ApiRequestBuilder setPath(String field) {
            path.put("field", field);
            return this;
        }

        public ApiRequestBuilder setMemberID(String id) {
            path.put("idMember", id);
            return this;
        }

        public TrelloBoardServiceObj buildRequest() {
            return new TrelloBoardServiceObj(parameters, requestMethod, path);
        }
    }
    //ENDING OF BUILDER PATTERN

    public Response sendRequestPath() {

        String pathS = "";

        for (String key : path.keySet()) {
            pathS += "{" + key + "}/";
        }

        return RestAssured
                .given(requestSpecification()).log().all()
                .pathParams(path)
                .queryParams(parameters)
                .request(requestMethod, pathS)
                .prettyPeek();
    }

    public static RequestSpecification requestSpecification() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)//
                .setAccept(ContentType.JSON)
                .setBaseUri(BOARDS_URL)
                .build();
    }

    public static List<TrelloBoardCompact> getAllBoards(Response response) {
        List<TrelloBoardCompact> answers = new Gson()
                .fromJson(response.asString().trim(), new TypeToken<List<TrelloBoardCompact>>() {
                }.getType());
        return answers;
    }

    public static TrelloBoardCompact getBoardById(Response response) {
        TrelloBoardCompact answers = new Gson()
                .fromJson(response.asString().trim(), new TypeToken<TrelloBoardCompact>() {
                }.getType());
        return answers;
    }

    public static TrelloNewBoard getNewBoard(Response response) {
        TrelloNewBoard answers = new Gson()
                .fromJson(response.asString().trim(), new TypeToken<TrelloNewBoard>() {
                }.getType());
        return answers;
    }

    public static List<TrelloBoardLabel> getLabels(Response response) {
        List<TrelloBoardLabel> answers = new Gson()
                .fromJson(response.asString().trim(), new TypeToken<List<TrelloBoardLabel>>() {
                }.getType());
        return answers;
    }

    public static String getResultString(Response response) {
        return response.asString().trim();
    }


    public static ResponseSpecification goodResponseSpecification() {
        return new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectResponseTime(lessThan(10000L))
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }

    public static ResponseSpecification badResponseSpecification() {
        return new ResponseSpecBuilder()
                .expectContentType(ContentType.TEXT)
                .expectResponseTime(lessThan(10000L))
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .build();
    }

}

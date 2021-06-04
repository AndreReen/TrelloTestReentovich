package core;

import beans.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import constants.EndPoints;
import constants.LabelColour;
import constants.UserTypes;
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

        public ApiRequestBuilder setName(String name) {
            parameters.put("name", name);
            return this;
        }

        public ApiRequestBuilder setLabelColor(LabelColour color) {
            parameters.put("color", color.colour);
            return this;
        }

        public ApiRequestBuilder setViewFields(String[] fields) {
            parameters.put("fields", Arrays.stream(fields).collect(Collectors.joining(",")));
            return this;
        }

        public ApiRequestBuilder setID(String id) {
            path.put("board_id", id);
            return this;
        }

        public ApiRequestBuilder setType(UserTypes type) {
            parameters.put("type", type.type);
            return this;
        }
        public ApiRequestBuilder allowBillableGuest(Boolean permission) {
            parameters.put("allowBillableGuest", permission.toString());
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

        ReadProperties.main();
        parameters.put("key", ReadProperties.props.get("key"));
        parameters.put("token", ReadProperties.props.get("token"));

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

    public static TrelloBoardCompact getBoardCompact(Response response) {
        TrelloBoardCompact answers = new Gson()
                .fromJson(response.asString().trim(), new TypeToken<TrelloBoardCompact>() {
                }.getType());
        return answers;
    }

    public static TrelloBoardFull getBoardFull(Response response) {
        TrelloBoardFull answers = new Gson()
                .fromJson(response.asString().trim(), new TypeToken<TrelloBoardFull>() {
                }.getType());
        return answers;
    }

    public static List<TrelloBoardLabel> getAllLabels(Response response) {
        List<TrelloBoardLabel> answers = new Gson()
                .fromJson(response.asString().trim(), new TypeToken<List<TrelloBoardLabel>>() {
                }.getType());
        return answers;
    }

    public static TrelloBoardLabel getLabel(Response response) {
        TrelloBoardLabel answers = new Gson()
                .fromJson(response.asString().trim(), new TypeToken<TrelloBoardLabel>() {
                }.getType());
        return answers;
    }

    public static FieldValue getField(Response response) {
        FieldValue answers = new Gson()
                .fromJson(response.asString().trim(), new TypeToken<FieldValue>() {
                }.getType());
        return answers;
    }

    public static List<BoardMember> getMembers(Response response) {
        List<BoardMember> answers = new Gson()
                .fromJson(response.asString().trim(), new TypeToken<List<BoardMember>>() {
                }.getType());
        return answers;
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

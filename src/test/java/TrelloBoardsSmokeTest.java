import beans.TrelloBoardCompact;
import beans.TrelloBoardLabel;
import beans.TrelloNewBoard;
import constants.EndPoints;
import constants.TestData;
import constants.userTypes;
import core.DataProviderForTrello;
import io.restassured.http.Method;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import java.util.List;

import static core.TrelloBoardServiceObj.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;


public class TrelloBoardsSmokeTest {

    private static String id;

    @BeforeSuite
    public static void setUp() {
    }

    @Test(priority = 1,
            dataProviderClass = DataProviderForTrello.class,
            dataProvider = "nameForBoard")
    public static void createBoard(String name) {
        TrelloNewBoard result = getNewBoard(
                requestBuilder()
                        .propsAuthorization()
                        .setName(name)
                        .setMethod(Method.POST)
                        .buildRequest()
                        .sendRequestPath());

        id = result.getId();
    }

    @Test(priority = 2)
    public static void updateBoardName() {
        requestBuilder()
                .propsAuthorization()
                .setName(TestData.SAMPLE_NAME2)
                .setMethod(Method.PUT)
                .setID(id)
                .buildRequest().sendRequestPath()
                .then().assertThat().spec(goodResponseSpecification())
                .body(containsString("\"name\":\"sigma\""));
    }

    @Test(priority = 3)
    public static void getBoardByID() {
        TrelloBoardCompact expected = new TrelloBoardCompact();
        expected.setId(id);
        expected.setName(TestData.SAMPLE_NAME2);

        TrelloBoardCompact result = getBoardById(
                requestBuilder()
                        .propsAuthorization()
                        .setFields(EndPoints.FIELDS)
                        .setID(id)
                        .buildRequest().sendRequestPath());

        assertThat(result, equalTo(expected));
    }

    @Test(priority = 4)
    public static void getFieldOnBoard() {
        requestBuilder()
                .propsAuthorization()
                .setID(id)
                .setPath(EndPoints.ID_ORGANIZATION)
                .buildRequest().sendRequestPath()
                .then().assertThat().spec(goodResponseSpecification()
                .body(containsString("\"_value\":\"" + TestData.MYORGID + "\"")));
    }

    @Test(priority = 5,
            dataProviderClass = DataProviderForTrello.class,
            dataProvider = "dataForLabels")
    public static void createLabelOnBoard(String name, String label) {
        requestBuilder()
                .propsAuthorization()
                .setLabelColor(label)
                .setName(name)
                .setID(id)
                .setPath(EndPoints.LABELS)
                .setMethod(Method.POST)
                .buildRequest().sendRequestPath()
                .then().assertThat().spec(goodResponseSpecification())
                .body(containsString("\"name\":\"" + name + "\""));
    }

    @Test(priority = 6)
    public static void getLabelsOnBoard() {
        List<TrelloBoardLabel> result = getLabels(
                requestBuilder()
                        .propsAuthorization()
                        .setID(id)
                        .setPath(EndPoints.LABELS)
                        .buildRequest().sendRequestPath());

        assertThat(result.get(result.size() - 1).getName(), equalTo(TestData.TEST_LABEL));
    }

    @Test(priority = 7,
            dataProviderClass = DataProviderForTrello.class,
            dataProvider = "userRights")
    public static void addMemberToBoard(String rights) {
        requestBuilder()
                .propsAuthorization()
                .setID(id)
                .setPath(EndPoints.MEMBERS)
                .setMemberID(TestData.MEMBER_ID)
                .setType(rights)
                .setMethod(Method.PUT)
                .buildRequest().sendRequestPath()
                .then().assertThat().spec(goodResponseSpecification());
    }

    @Test(priority = 8)
    public static void getMembersOnBoard() {
        requestBuilder()
                .propsAuthorization()
                .setID(id)
                .setPath(EndPoints.MEMBERS)
                .buildRequest().sendRequestPath()
                .then().assertThat().spec(goodResponseSpecification());
    }

    @Test(priority = 9)
    public static void removeMemberFromBoard() {
        requestBuilder()
                .propsAuthorization()
                .setID(id)
                .setPath(EndPoints.MEMBERS)
                .setMemberID(TestData.MEMBER_ID)
                .setMethod(Method.DELETE)
                .buildRequest().sendRequestPath()
                .then().assertThat().spec(goodResponseSpecification());

    }

    @Test(priority = 10)
    public static void deleteBoardTest() {
        requestBuilder()
                .propsAuthorization()
                .setMethod(Method.DELETE)
                .setID(id)
                .buildRequest()
                .sendRequestPath()
                .then().assertThat()
                .spec(goodResponseSpecification()
                        .body(containsString("{\"_value\":null}")));

    }
}

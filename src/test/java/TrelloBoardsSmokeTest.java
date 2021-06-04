import beans.*;
import constants.*;
import core.DataProviderForTrello;
import io.restassured.http.Method;
import matchers.DefaultColor;
import org.apache.http.HttpStatus;
import org.testng.annotations.*;

import java.util.List;

import static core.TrelloBoardServiceObj.*;
import static matchers.DefaultColor.defaultColor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class TrelloBoardsSmokeTest {

    private static String id;

    @BeforeMethod()
    public static void setUp() {
        TrelloBoardFull newTestBoard = getBoardFull(
                requestBuilder()
                        .setName(TestData.SAMPLE_NAME1)
                        .setMethod(Method.POST)
                        .buildRequest()
                        .sendRequestPath());
        id = newTestBoard.getId();

        assertThat(id, notNullValue());
    }

    @AfterMethod()
    public static void tearDown() {
        requestBuilder()
                .setMethod(Method.DELETE)
                .setID(id)
                .buildRequest()
                .sendRequestPath()
                .then().assertThat()
                .spec(goodResponseSpecification());

        //assert board is deleted
        requestBuilder()
                .setMethod(Method.GET)
                .setID(id)
                .buildRequest()
                .sendRequestPath()
                .then().assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }


    @Test
    public static void updateBoardName() {
        TrelloBoardCompact expectedBoard = new TrelloBoardCompact();
        expectedBoard.setId(id);
        expectedBoard.setName(TestData.SAMPLE_NAME2);

        TrelloBoardCompact boardUpatedName = getBoardCompact(
                requestBuilder()
                        .setName(TestData.SAMPLE_NAME2)
                        .setMethod(Method.PUT)
                        .setID(id)
                        .buildRequest().sendRequestPath()
                        .then().assertThat().spec(goodResponseSpecification())
                        .extract().response());

        assertThat(boardUpatedName, equalTo(expectedBoard));
    }

    @Test
    public static void getBoardByID() {
        TrelloBoardCompact expectedBoard = new TrelloBoardCompact();
        expectedBoard.setId(id);
        expectedBoard.setName(TestData.SAMPLE_NAME1);

        TrelloBoardCompact boardFromRequest = getBoardCompact(
                requestBuilder()
                        .setViewFields(EndPoints.CUSTOM_VIEW)
                        .setID(id)
                        .buildRequest().sendRequestPath()
                        .then().assertThat().spec(goodResponseSpecification())
                        .extract().response());

        assertThat(boardFromRequest, equalTo(expectedBoard));
    }


    //Returns single field ONLY
    //Uses endpoint to fetch field value
    @Test(dataProviderClass = DataProviderForTrello.class,
            dataProvider = "fields")
    public static void getFieldOnBoard(String field, String value) {
        FieldValue expectedFieldValue = new FieldValue();
        expectedFieldValue.setValue(value);

        FieldValue extractedField = getField(
                requestBuilder()
                        .setID(id)
                        .setPath(field)
                        .buildRequest().sendRequestPath()
                        .then().assertThat().spec(goodResponseSpecification()).extract().response());
        assertThat(extractedField, equalTo(expectedFieldValue));
    }

    @Test(dataProviderClass = DataProviderForTrello.class,
            dataProvider = "dataForLabels")
    public static void createLabelOnBoard(String name, LabelColour labelColor) {
        TrelloBoardLabel labelExpected = new TrelloBoardLabel();
        labelExpected.setColor(labelColor.colour);
        labelExpected.setIdBoard(id);
        labelExpected.setName(name);

        TrelloBoardLabel labelRequested = getLabel(requestBuilder()
                .setLabelColor(labelColor)
                .setName(name)
                .setID(id)
                .setPath(EndPoints.LABELS)
                .setMethod(Method.POST)
                .buildRequest().sendRequestPath()
                .then().assertThat().spec(goodResponseSpecification())
                .extract().response());

        assertThat(labelRequested, equalTo(labelExpected));
    }

    @Test
    public static void getLabelsOnBoard() {

        List<TrelloBoardLabel> labels = getAllLabels(
                requestBuilder()
                        .setID(id)
                        .setPath(EndPoints.LABELS)
                        .buildRequest().sendRequestPath()
                        .then().assertThat().spec(goodResponseSpecification())
                        .extract().response());

        //Assert that there are 6 default labels on board
        assertThat(labels.size(), equalTo(6));
        //Assert created labels have default colors
        for (TrelloBoardLabel label : labels) {
            assertThat(label, defaultColor());
        }
    }

    @Test
    public static void getMembersOnBoard() {
        BoardMember expectedMember = new BoardMember();
        expectedMember.setId(OwnerCredentials.ID);
        expectedMember.setUsername(OwnerCredentials.USERNAME);
        expectedMember.setFullName(OwnerCredentials.FULL_NAME);

        List<BoardMember> boardMembersActual = getMembers(requestBuilder()
                .setID(id)
                .setPath(EndPoints.MEMBERS)
                .buildRequest().sendRequestPath()
                .then().assertThat().spec(goodResponseSpecification())
                .extract().response());

        //Asserting that the only member in the list is the Owner of the board
        assertThat(boardMembersActual.size(), equalTo(1));
        assertThat(boardMembersActual.get(0), equalTo(expectedMember));
    }

    @Test(dataProviderClass = DataProviderForTrello.class,
            dataProvider = "userRights")
    public static void addAndRemoveMemberFromBoard(UserTypes rights) {

        requestBuilder()
                .setID(id)
                .setPath(EndPoints.MEMBERS)
                .setMemberID(TestData.MEMBER_ID)
                .setType(rights)
                .allowBillableGuest(true)
                .setMethod(Method.PUT)
                .buildRequest().sendRequestPath()
                .then().assertThat().spec(goodResponseSpecification());

        requestBuilder()
                .setID(id)
                .setPath(EndPoints.MEMBERS)
                .setMemberID(TestData.MEMBER_ID)
                .setMethod(Method.DELETE)
                .buildRequest().sendRequestPath()
                .then().assertThat().spec(goodResponseSpecification());

        List<BoardMember> boardMembersActual = getMembers(requestBuilder()
                .setID(id)
                .setPath(EndPoints.MEMBERS)
                .buildRequest().sendRequestPath()
                .then().assertThat().spec(goodResponseSpecification())
                .extract().response());

        assertThat(boardMembersActual.size(), equalTo(1));
    }
}

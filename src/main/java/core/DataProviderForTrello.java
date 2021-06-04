package core;

import constants.*;
import org.testng.annotations.DataProvider;

import static constants.LabelColour.*;

public class DataProviderForTrello {


    @DataProvider
    public Object[][] nameForBoard() {
        return new Object[][]{
                {TestData.SAMPLE_NAME1},
        };
    }

    @DataProvider
    public Object[][] dataForLabels() {
        return new Object[][]{
                {TestData.TEST_LABEL + "1",GREEN},
                {TestData.TEST_LABEL + "2",YELLOW},
                {TestData.TEST_LABEL + "3",ORANGE},
                {TestData.TEST_LABEL + "4",RED},
                {TestData.TEST_LABEL + "5",PURPLE},
                {TestData.TEST_LABEL + "6",BLUE},
        };
    }

    @DataProvider
    public Object[][] userRights() {
        return new Object[][]{
                {userTypes.ADMIN},
                {userTypes.NORMAL},
                {userTypes.OBSERVER},
        };
    }

    @DataProvider
    public Object[][] fields() {
        return new Object[][]{
                {EndPoints.ID_ORGANIZATION, TestData.MYORGID},
                {EndPoints.NAME, TestData.SAMPLE_NAME1},
                {EndPoints.STARRED, TestData.FALSE},
        };
    }

}

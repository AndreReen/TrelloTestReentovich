package core;

import constants.LabelColors;
import constants.TestData;
import constants.userTypes;
import org.testng.annotations.DataProvider;

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
                {TestData.TEST_LABEL + "1" , LabelColors.GREEN},
                {TestData.TEST_LABEL + "2" , LabelColors.YELLOW},
                {TestData.TEST_LABEL , LabelColors.RED},
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

}

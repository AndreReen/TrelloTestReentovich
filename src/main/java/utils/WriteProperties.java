package utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class WriteProperties {


    public static void main(String[] args) {

        try (OutputStream output = new FileOutputStream("src/main/resources/testProps.properties")) {

            Properties prop = new Properties();

            prop.setProperty("key", "dde05163bed8460fc3b15c031ff27bb5");
            prop.setProperty("token", "c474488e52aecca2cf1270d2dd3d4b17b4b51f1ceb641614f5427436ab21158c");

            prop.store(output, null);

            System.out.println(prop);

        } catch (IOException io) {
            io.printStackTrace();
        }

    }
}

package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

public class ReadProperties {

    public static HashMap<String, String> props;


    public static void main() {
        props = new HashMap<>();
        ReadProperties app = new ReadProperties();
        app.printAll("testProps.properties");
    }

    private void printAll(String filename) {

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(filename)) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find " + filename);
                return;
            }

            prop.load(input);

            for (String key : prop.stringPropertyNames()) {
                props.put(key, prop.getProperty(key));
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}

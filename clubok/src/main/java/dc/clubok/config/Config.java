package dc.clubok.config;

import java.io.*;
import java.util.Properties;

public class Config {
    private Properties properties;

    public Config() {
        String env = (System.getenv("JAVA_ENV") != null) ? System.getenv("JAVA_ENV") : "development";
        properties = new Properties();

        InputStream input;
        try {
            if (env.equals("development")) {
                input = new FileInputStream("./src/main/java/dc/clubok/config/development.properties");
                properties.load(input);
            } else if (env.equals("test")) {
                input = new FileInputStream("./src/main/java/dc/clubok/config/test.properties");
                properties.load(input);
            } else {
                properties.setProperty("port", System.getenv("PORT"));
                properties.setProperty("mongodb_uri", System.getenv("MONGODB_URI"));
                properties.setProperty("secret", System.getenv("SECRET"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Properties getProperties() {
        return properties;
    }
}

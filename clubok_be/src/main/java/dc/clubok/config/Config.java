package dc.clubok.config;

import java.io.*;
import java.util.Properties;

public class Config {
    private static Properties properties;

    public Config() {
        String env = (System.getenv("JAVA_ENV") != null) ? System.getenv("JAVA_ENV") : "development";
        properties = new Properties();

        InputStream input;
        try {
            switch (env) {
                case "development":
                    input = new FileInputStream("./clubok_be/src/main/java/dc/clubok/config/development.properties");
                    properties.load(input);
                    break;
                case "test":
                    input = new FileInputStream("./src/main/java/dc/clubok/config/test.properties");
                    properties.load(input);
                    break;
                default:
                    properties.setProperty("port", System.getenv("PORT"));
                    properties.setProperty("mongodb_uri", System.getenv("MONGODB_URI"));
                    properties.setProperty("secret", System.getenv("SECRET"));
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Properties getProperties() {
        return properties;
    }
}

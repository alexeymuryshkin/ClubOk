package dc.clubok.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class Config {
    private static Properties properties;

    public Config() {
        Map<String, String> envVars = new ProcessBuilder().environment();
        String env = (envVars.get("JAVA_ENV") != null) ? envVars.get("JAVA_ENV") : "development";
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
                    properties.setProperty("port", envVars.get("PORT"));
                    properties.setProperty("mongodb_uri", envVars.get("MONGODB_URI"));
                    properties.setProperty("secret", envVars.get("JWT_SECRET"));
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

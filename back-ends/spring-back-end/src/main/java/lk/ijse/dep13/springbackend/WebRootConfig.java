package lk.ijse.dep13.springbackend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.annotation.RequestScope;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
@PropertySource("classpath:/application.properties") // This is for Connects a Resource Bundle
public class WebRootConfig {

    public WebRootConfig() throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
    }

//    @Scope("prototype") // This is for using for again again
    @RequestScope
    @Bean
    // @Value for get the resource bundle data
    public Connection connection(@Value("${app.datasource.url}") String url,
                                 @Value("${app.datasource.username}") String username,
                                 @Value("${app.datasource.password}") String password) throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/dep13_note_app", "postgres", "psql");
    }
}
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;

public class ProfilePictureGeneratorTest {

    @Test
    void generateBase64DataUrl() throws IOException {
        byte[] bytes = getClass()
                .getResourceAsStream("/profile-picture.png")
                .readAllBytes();
        String base64Url = Base64.getEncoder().encodeToString(bytes);
        System.out.println("data:image/png;base64," + base64Url);
    }
}

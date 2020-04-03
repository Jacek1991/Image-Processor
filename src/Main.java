import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Main {



    public static void main(String[] args) {

        URL url = null;

        try {
            url = new URL("https://f2.photoblog.pl/fbl-2011/201107/99443377.jpg");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        BufferedImage image = null;
        try {
            image = ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Frame(image).setVisible(true);
    }
}

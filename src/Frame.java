
import org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;

public class Frame extends JFrame {

    private static BufferedImage resize(BufferedImage img, int height, int width) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }


    public Frame(BufferedImage image) throws HeadlessException {
        this.image = image;
        initComponents();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        thisImage = image;
        this.imageToDraw = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics g = imageToDraw.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        isGrey = false;
        isNegative = false;
    }

    void initComponents()
    {

        imagePanel.setIcon(new ImageIcon(resize(image,(int)(500 * ((double)image.getHeight()/(double)image.getWidth())), 500)));
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
        layout.createSequentialGroup()
        .addComponent(imagePanel)
        .addGroup(
                layout.createParallelGroup()
                .addComponent(negativeButton)
                .addComponent(grayButton)
                        .addComponent(brightnessLabel)
                .addComponent(brightnessSlider)
                .addComponent(saturationLabel)
                .addComponent(saturationSlider)
                .addComponent(fileInput)
                        .addComponent(urlButton)
                .addComponent(loadFileButton)
                .addComponent(saveFileButton)

        )
        )
        ;
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addComponent(imagePanel)
                .addGroup(
                        layout.createSequentialGroup()
                                .addComponent(negativeButton)
                                .addComponent(grayButton)
                                .addComponent(brightnessLabel)
                                .addComponent(brightnessSlider)
                                .addComponent(saturationLabel)
                                .addComponent(saturationSlider)
                                .addComponent(fileInput)
                                .addComponent(urlButton)
                                .addComponent(loadFileButton)
                                .addComponent(saveFileButton)
                                .addContainerGap(100,1200)

                )
        );
        fileInput.setForeground(Color.gray);
        fileInput.setText("Podaj adres URL obrazu");
        fileInput.addFocusListener(new FocusListener() {


            @Override
            public void focusGained(FocusEvent focusEvent) {
                fileInput.setForeground(Color.black);
                fileInput.setText("");
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
                fileInput.setForeground(Color.gray);
                fileInput.setText("Podaj adres URL obrazu");
            }


        });
        brightnessSlider.setMajorTickSpacing(255);
        saturationSlider.setMajorTickSpacing(100);
        brightnessSlider.setPaintTicks(true);
        saturationSlider.setPaintTicks(true);
        grayButton.addActionListener(new ActionListener() {


            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                isGrey = !isGrey;
                renderImage();
            }
        });
        negativeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                isNegative = !isNegative;
                renderImage();
            }
        });
        brightnessSlider.addChangeListener(new ChangeListener() {

            int previousValue = brightnessSlider.getValue();

            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                    if (!brightnessSlider.getValueIsAdjusting())
                   renderImage();

            }});
        saturationSlider.addChangeListener(new ChangeListener() {
            @Override

            public void stateChanged(ChangeEvent changeEvent) {
                if (!saturationSlider.getValueIsAdjusting())
                renderImage();

            }});

        fileChooser.setDialogTitle("Wybierz obraz");
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Pliki obrazu", "png", "jpg");
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        loadFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                if(fileChooser.showOpenDialog(rootPane) == 0)
                {
                    File file = fileChooser.getSelectedFile();
                    try {
                        BufferedImage img = ImageIO.read(file);
                        thisImage = img;
                        isNegative = false;
                        isGrey = false;
                        saturationSlider.setValue(0);
                        brightnessSlider.setValue(0);
                        renderImage();
                        Frame.this.pack();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        saveFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
                if(fileChooser.showSaveDialog(rootPane) == 0)
                {
                    String last4 = fileChooser.getSelectedFile().getAbsolutePath().substring(fileChooser.getSelectedFile().getAbsolutePath().length()-4);
                    System.out.println(last4);
                    String extension = (last4.equals(".png") || last4.equals(".jpg"))? "" : imageToDraw.getType()==5? ".jpg": ".png";
                    File file = new File(fileChooser.getSelectedFile().getAbsolutePath()+ extension);
                    System.out.println(imageToDraw.getType());
                    System.out.println(file.getPath());

                    try {
                        ImageIO.write(imageToDraw, imageToDraw.getType()==5? "jpg": "png", file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        urlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                URL url = null;

                try {
                    url = new URL(fileInput.getText());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                try {
                    BufferedImage img = ImageIO.read(url);
                    thisImage = img;
                    isNegative = false;
                    isGrey = false;
                    saturationSlider.setValue(0);
                    brightnessSlider.setValue(0);
                    renderImage();
                    Frame.this.pack();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        this.pack();
    }




    BufferedImage image;
    JButton negativeButton = new JButton("Negatyw");
    JButton grayButton = new JButton("Szarość");

    JLabel imagePanel = new JLabel();
    JSlider brightnessSlider = new JSlider(-255,255,0);
    JSlider saturationSlider = new JSlider(-100,100,0);
    JLabel brightnessLabel = new JLabel("Jasność");
    JLabel saturationLabel = new JLabel("Nasycenie barw");
    JTextField fileInput = new JTextField();
    JButton urlButton = new JButton("Wczytaj z URL");
    JButton loadFileButton = new JButton("Wczytaj z pliku");
    JButton saveFileButton = new JButton("Zapisz do pliku");

    public volatile BufferedImage thisImage;
    public volatile BufferedImage imageToDraw;
    public volatile BufferedImage previousImage;
    public volatile boolean isGrey;
    public volatile boolean isNegative;
    JFileChooser fileChooser = new JFileChooser();
    class ChangeBrightnessRunnable implements Runnable
    {
        BufferedImage image;
        int value;

        public ChangeBrightnessRunnable(BufferedImage image, int value) {
            this.image = image;
            this.value = value;
        }

        @Override
        public void run() {
            BufferedImage newImage = new BufferedImage(this.image.getWidth(), this.image.getHeight(), this.image.getType());
            Graphics g = newImage.getGraphics();
            g.drawImage(this.image, 0, 0, null);
            g.dispose();
            int height = newImage.getHeight();
            int width = newImage.getWidth();

            for(int i=0; i<height; i++) {

                for(int j=0; j<width; j++) {

                    Color c = new Color(newImage.getRGB(j, i));
                    Color newColor = new Color(c.getRed() + value > 255 ? 255: c.getRed() + value < 0 ? 0 : c.getRed() + value, c.getGreen() + value > 255 ? 255: c.getGreen() + value < 0 ? 0 : c.getGreen() + value,c.getBlue() + value > 255 ? 255: c.getBlue() + value < 0 ? 0 : c.getBlue() + value);
                    newImage.setRGB(j, i, newColor.getRGB() );

                }
            }
            imageToDraw = newImage;
        }
    }
    class ChangeSaturationRunnable implements Runnable
    {
        BufferedImage image;
        int value;

        public ChangeSaturationRunnable(BufferedImage image, int value) {
            this.image = image;
            this.value = value;
        }

        @Override
        public void run() {
            BufferedImage newImage = new BufferedImage(this.image.getWidth(), this.image.getHeight(), this.image.getType());
            Graphics g = newImage.getGraphics();
            g.drawImage(this.image, 0, 0, null);
            g.dispose();
            int height = newImage.getHeight();
            int width = newImage.getWidth();

            for(int i=0; i<height; i++) {

                for(int j=0; j<width; j++) {

                    Color c = new Color(newImage.getRGB(j, i));
                    float [] hsv = Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(),null);
                    hsv[1] = hsv[1] + value/100.0 > 1? 1 : (float) (hsv[1] + value / 100.0 < 0 ? 0 : hsv[1] + value / 100.0);

                    Color newColor = new Color(Color.HSBtoRGB(hsv[0],hsv[1],hsv[2]));
                    newImage.setRGB(j, i, newColor.getRGB() );

                }

            }
            imageToDraw = newImage;
        }
    }
    class GreyRunnable implements Runnable
    {
        BufferedImage image;


        public GreyRunnable(BufferedImage image) {
            this.image = image;

        }

        @Override
        public void run() {

            BufferedImage newImage = new BufferedImage(this.image.getWidth(), this.image.getHeight(), this.image.getType());
            Graphics g = newImage.getGraphics();
            g.drawImage(this.image, 0, 0, null);
            g.dispose();
            int height = newImage.getHeight();
            int width = newImage.getWidth();

            for(int i=0; i<height; i++) {

                for(int j=0; j<width; j++) {

                    Color c = new Color(newImage.getRGB(j, i));
                    Color newColor = new Color((int)(0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue()), (int)(0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue()), (int)(0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue()));
                    newImage.setRGB(j, i, newColor.getRGB() );

                }
            }

                imageToDraw = newImage;


        }
    }

    class NegativeRunnable implements Runnable
    {
        BufferedImage image;


        public NegativeRunnable(BufferedImage image) {
            this.image = image;

        }

        @Override
        public void run() {

            BufferedImage newImage = new BufferedImage(this.image.getWidth(), this.image.getHeight(), this.image.getType());
            Graphics g = newImage.getGraphics();
            g.drawImage(this.image, 0, 0, null);
            g.dispose();
            int height = newImage.getHeight();
            int width = newImage.getWidth();

            for(int i=0; i<height; i++) {

                for(int j=0; j<width; j++) {

                    Color c = new Color(newImage.getRGB(j, i));
                    Color newColor = new Color(255 - c.getRed(), 255 - c.getGreen(),255-  c.getBlue());
                    newImage.setRGB(j, i, newColor.getRGB() );


                }
            }

            imageToDraw = newImage;


        }
    }

    void renderImage()
    {

        Thread thread = new Thread(new ChangeSaturationRunnable(thisImage, saturationSlider.getValue()));
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread thread2 = new Thread(new ChangeBrightnessRunnable(imageToDraw, brightnessSlider.getValue()));
        thread2.start();

        try {
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (isGrey)
        {
            Thread thread3 = new Thread(new GreyRunnable(imageToDraw));
            thread3.start();

            try {
                thread3.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (isNegative)
        {
            Thread thread4 = new Thread(new NegativeRunnable(imageToDraw));
            thread4.start();

            try {
                thread4.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        imagePanel.setIcon(new ImageIcon(resize(imageToDraw,(int)(500 * ((double)imageToDraw.getHeight()/(double)imageToDraw.getWidth())), 500)));
    }

}

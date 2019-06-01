import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
public class tantum_Numerus_Main {
    public static final int WIDTH=20;
    public static final int HEIGHT=20;
    public static void imageFixer(String file){
        File f=null;
        BufferedImage img=null;
        try{
            f = new File("Images\\"+file+".png");
            img = ImageIO.read(f);
            grayScale(img);
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
        Image tmp = img.getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        try{
            f = new File("C:\\Users\\NerdMachine2\\IdeaProjects\\tantumerous\\Images\\"+file+"_output.png");
            ImageIO.write(resized, "png", f);
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
    private static void grayScale(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int p = image.getRGB(x,y);
                int a = (p>>24)&0xff;
                int r = (p>>16)&0xff;
                int g = (p>>8)&0xff;
                int b = p&0xff;
                int avg = (r+g+b)/3;
                p = (a<<24) | (avg<<16) | (avg<<8) | avg;
                image.setRGB(x, y, p);
            }
        }
    }

    public static void main(String[] args) {
        imageFixer("num2");
    }

}

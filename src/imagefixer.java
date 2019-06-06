//code for extracting individual colors taken from https://stackoverflow.com/questions/2615522/java-bufferedimage-getting-red-green-and-blue-individually
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ICC_ProfileRGB;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class imagefixer {

    //returns an array of ints, which represent the darkness level of each pixel
    public int[][] getImage(int x, int y, String imagePath) throws FileNotFoundException {

        int[][] returnArray = new int[x][y];
        float xRatio;
        float yRatio;



        //after this block, thePicture will be a BufferedImage loaded with the given imagepath
        BufferedImage thePicture;
        try {
            thePicture = ImageIO.read(new File(imagePath));
        } catch (IOException ex){throw new FileNotFoundException("couldn't find image file");}

        //finds the ratio of the array's size to the pictures size
        xRatio = thePicture.getNumXTiles();
        xRatio /= x;
        yRatio = thePicture.getNumYTiles();
        yRatio /= y;


        //this chunk should get the appropriate pixels from the image and map them to the array.
        //It uses individual pixels, not averages of chunks of pixels, so this method may need to
        //be reworked.
        for(int xcord = 0; xcord < returnArray.length; xcord++){
            for(int ycord = 0; y < returnArray[ycord].length; ycord++){

                int rgb = thePicture.getRGB(((int)(xcord * xRatio)), (int)(yRatio * ycord));

                //this code was borrowed from https://stackoverflow.com/questions/2615522/java-bufferedimage-getting-red-green-and-blue-individually
                int red = (rgb >> 16) & 0x000000FF;
                int green = (rgb >>8 ) & 0x000000FF;
                int blue = (rgb) & 0x000000FF;

                returnArray[xcord][ycord] = (red + blue + green);
            }
        }




        return returnArray;
    }





}

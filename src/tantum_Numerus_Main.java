import java.awt.*;
import java.io.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import javax.imageio.ImageIO;
public class tantum_Numerus_Main {
    public static final int WIDTH=20;
    public static final int HEIGHT=20;
    public static final int SHADING=3; //used only for the benefit of the human looking at the resized images.
//    public static void imageFixer(String file){
//        File f=null;
//        BufferedImage img=null;
//        try{
//            f = new File("Images\\"+file+".png");
//            img = ImageIO.read(f);
//        }catch(IOException e){
//            System.out.println(e.getMessage());
//        }
//        BufferedImage resized=resize(img);
//        grayScale(resized);
//        //maybe put images in a folder in the git repository? then we don't need paths to places on your computer
//        try{
//            f = new File("C:\\Users\\NerdMachine2\\IdeaProjects\\tantumerous\\Images\\"+file+"_output.png");
//            ImageIO.write(resized, "png", f);
//        }catch(IOException e){
//            System.out.println(e.getMessage());
//        }
//    }
    public static void grayScale(BufferedImage image){
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
                if(avg!=255)
                    avg/=SHADING;
                p = (a<<24) | (avg<<16) | (avg<<8) | avg;
                image.setRGB(x, y, p);
            }
        }
    }

    public static BufferedImage resize(BufferedImage img){
        Image tmp = img.getScaledInstance(WIDTH, HEIGHT,Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }



    public static void main(String[] args) throws Exception{

        Scanner in = new Scanner(System.in);
        String testOrTrain = in.next();
        if(testOrTrain.equals("traingood")){
            String lastGenState = "SavedStates/lastGenState.txt";
            ArrayList<String> fileNames = new ArrayList<>();
            ArrayList<Integer> fileVals = new ArrayList<>();
            int lastSuccess = 0;
            int currentSuccess =0;
            System.out.println("how many images of each type would you like");

            int num= in.nextInt();
            for (int i = 0; i <2 ; i++) {
                File folder = new File("data_set/"+i);
                File[] listOfFiles = folder.listFiles();
                for (int k = 0; k < num; k++) {
                    if (listOfFiles[k].isFile()) {
                        fileNames.add(i+"/"+listOfFiles[k].getName());
                        fileVals.add(i);
                    }
                }
            }
            Random rand=new Random();
            rand.setSeed(1);
            Collections.shuffle(fileNames,rand);
            rand.setSeed(1);
            Collections.shuffle(fileVals,rand);
            neural_Net hellaBigBrain = new neural_Net(2,20,20);
            //hellaBigBrain.loadState("SavedStates/saveStateInUse.txt");
            hellaBigBrain.savestate(lastGenState);

            //training loop
                currentSuccess=0;
                int properOut;
                double[] givenOut;
                BufferedImage currentImage = ImageIO.read(new File("data_set/one1.png"));
                Integer[] expout=new Integer[fileVals.size()];
                String[] filelist=new String[fileNames.size()];
                fileNames.toArray(filelist);
                fileVals.toArray(expout);
                System.out.println( fileNames.size());
                hellaBigBrain.backprop(filelist,expout);
                for (int i = 0; i < fileNames.size(); i++){

                    properOut = fileVals.get(i);

                    try {
                        currentImage = ImageIO.read(new File("data_set/" + fileNames.get(i)));
                    } catch (Exception ex){
                        System.out.println("broke at file: " + fileNames.get(i));
                    }
                    grayScale(currentImage);
                    currentImage = resize(currentImage);

                    givenOut = hellaBigBrain.guessthatnumber(currentImage);

                    System.out.println("num: " + givenOut[0]+" certainty:"+givenOut[1]+"% expected:"+properOut);

                    if(givenOut[0] == properOut){
                        currentSuccess++;
                    }

                }
            System.out.println("I did " + currentSuccess + " wins today, thanks dad that's "+((double)currentSuccess/fileNames.size())*100+"%");

        }

        //trains the NN with Owen's homemade training algorithm: do random sh1t till it works :D
        if(testOrTrain.equals("train")){

            String lastGenState = "SavedStates/lastGenState.txt";
            ArrayList<String> fileNames = new ArrayList<>();
            ArrayList<Integer> fileVals = new ArrayList<>();
            int lastSuccess = 0;
            int currentSuccess =0;

            BufferedReader fileReader = new BufferedReader(new FileReader("data_set/fileNames"));
            String line = fileReader.readLine();
            while(line != null){
                fileNames.add(line.substring(0,line.length()-2));
                fileVals.add(Integer.parseInt(line.substring(line.length()-1)));
                line = fileReader.readLine();
            }

            neural_Net hellaBigBrain = new neural_Net(4,20,20);
            //hellaBigBrain.loadState("SavedStates/saveStateInUse.txt");
            hellaBigBrain.savestate(lastGenState);


            //training loop
            while(lastSuccess < 18){

                hellaBigBrain.randomMutate();
                currentSuccess=0;
                int properOut;
                double[] givenOut;
                BufferedImage currentImage = ImageIO.read(new File("data_set/one1.png"));

                for (int i = 0; i < 50; i++){


                    properOut = fileVals.get(i);

                    try {
                        currentImage = ImageIO.read(new File("data_set/" + fileNames.get(i)));
                    } catch (Exception ex){
                        System.out.println("broke at file: " + fileNames.get(i));
                    }
                    grayScale(currentImage);
                    currentImage = resize(currentImage);

                    givenOut = hellaBigBrain.guessthatnumber(currentImage);

                    System.out.println("num: " + givenOut[0]);


                    if(givenOut[0] == properOut){
                        currentSuccess++;
                    }
                }
                System.out.println("I did " + currentSuccess + " wins today, thanks dad");



                if(lastSuccess<=currentSuccess){
                    hellaBigBrain.savestate(lastGenState);
                    lastSuccess = currentSuccess;
                }
                else{
                    hellaBigBrain.loadState(lastGenState);
                }

                if(currentSuccess >= 45)
                    break;


            }


        }

        //idk what this should do
        if(testOrTrain.equals("test")){
            System.out.println("what file would you like me to look at?");
        }

    }

}

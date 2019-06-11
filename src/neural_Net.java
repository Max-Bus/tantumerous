import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.stream.Stream;




//hello, I changed a thing, you initially spelled "neurons" wrong garcia
public class neural_Net {
    public Neuron[][] neurons;
    private int layers;
    public final int width;
    public final int height;
    public final int outputnodes=2;
    public neural_Net(int layers,int w, int h){
        width=w;
        this.layers=layers;
        height=h;
        neurons=new Neuron[layers][];
        neurons[0]=new Neuron[w*h];
        for (int i = 0; i <neurons[0].length ; i++) {
            neurons[0][i]=new Neuron(0,0);
        }
        for (int i = 1; i <neurons.length ; i++) {

            if(i == neurons.length-1){
                neurons[i] = new Neuron[outputnodes];
            }
            else {
                neurons[i] = new Neuron[(int) Math.sqrt(neurons[i - 1].length) + outputnodes];
            }
            for (int j = 0; j < neurons[i].length; j++) {
                neurons[i][j] = new Neuron(0, neurons[i - 1].length);
                neurons[i][j].scramble();
            }

        }
    }


    //last resort evolution thing
    public void randomMutate(){
        for(int i = 0; i < neurons.length; i++){
            for(int r  = 0; r < neurons[i].length; r++){
                neurons[i][r].randomMutation();
            }
        }
    }




    //Owen's plan for this function: determine a way to save the state to a text file
    //(wow, very surprising)
    //Owen's current plan for datafile formatting:
    //  text file, each new line is a layer. Each layer contains a bunch of lists of doubles, separated by commas
    //  the layers go front to back (assuming you read top to bottom)
    // a new comma represents a new node
    //the floats in between the commas are the weights between neurons
    // colons separate weights from biases 1 1 1:1 1 1  left: weights, right: biases
    //everything should end with commas, to help this program tie things up nice and neat
    public void savestate(String datafile) throws  IOException{
        BufferedWriter out = new BufferedWriter(new FileWriter(datafile));

        for(int layer = 0; layer < neurons.length; layer++){

            //this for loop prints layers to the output file
            for(int neuron = 0; neuron < neurons[layer].length; neuron++){


                if(neurons[layer][neuron].weights.length > 0) {
                    //these for loops do the output for individual neurons
                    for (int weight = 0; weight < neurons[layer][neuron].weights.length - 1; weight++) {
                        out.write(Double.toString(neurons[layer][neuron].weights[weight]));
                        out.write(" ");
                    }
                    out.write(Double.toString(neurons[layer][neuron].weights[neurons[layer][neuron].weights.length - 1]));
                }

                out.write(':');

                if(neurons[layer][neuron].weights.length > 0) {
                        out.write(Double.toString(neurons[layer][neuron].bias));
                }

                out.write(",");
            }
            out.newLine();
        }
    }




    //needs code review desperately
    //generates a neural Net from a given text file
    //IMPORTANT
    //this function will override all of the data that the neural net has stored in memory, so it should
    //only be performed after saving the current state
    public void loadState(String datafile) throws IOException {


        //Hi, I'm the 3d array fairy
        //these 3d arraylists are nasty looking
        //they are structured as: layer, node, weights/biases
        //*fairies away*
        ArrayList<ArrayList<ArrayList<Double>>> weightsForNodes = new ArrayList<>();
        ArrayList<ArrayList<Double>> biasesForNodes = new ArrayList<>();


        BufferedReader reader = new BufferedReader(new FileReader(datafile));
        String line = reader.readLine();
        StringBuilder lineInterpreter = new StringBuilder();
        int currentLayer=0;
       
        
        //this arraylist will be used in the big loops below,
        //to cache the doubles that are separated by spaces in the file
        ArrayList<Double> cachedList = new ArrayList<>();

     

        //the String line is a line from input,
        //and this whole while loop builds the data needed to build the net
        //after it executes, weightsForNodes and biasesForNodes will be filled nice and neat
        while(line != null){

            weightsForNodes.add(new ArrayList<>());
            biasesForNodes.add(new ArrayList<>());

            
            for(int character = 0; character < line.length(); character++){
                //this throws all of the doubles into an arraylist, which will make life easier
                if(line.charAt(character) == ' '){
                    cachedList.add(Double.parseDouble(lineInterpreter.toString()));
                    lineInterpreter.delete(0,lineInterpreter.length());
                }
                else if(line.charAt(character) == ':'){
                    weightsForNodes.get(currentLayer).add(cachedList);
                    //if we get an error here, change it to length-1
                    lineInterpreter.delete(0,lineInterpreter.length());
                    cachedList = new ArrayList<Double>();                    
                }
                else if(line.charAt(character) == ','){
                    biasesForNodes.get(currentLayer).add(cachedList.get(0));
                    //if we get an error here, change it to length-1
                    lineInterpreter.delete(0,lineInterpreter.length());
                    cachedList = new ArrayList<>();

                }
                else{
                lineInterpreter.append(line.charAt(character));
                }
            }

            line = reader.readLine();
            currentLayer++;
        }
        
        //at this point in the code, my lovely arraylists should be bursting with data.
        //Now to build the actual neural net!
        
        neurons = new Neuron[weightsForNodes.size()][weightsForNodes.get(0).size()];
        for(int layer = 0; layer < neurons.length; layer++){
            for(int neuron = 0; neuron < neurons[layer].length; neuron++){
                //this instantiator will make you sad, but dw, I promise it makes sense

                //this is an unwrapper
                double[] newWeights = new double[weightsForNodes.get(layer).get(neuron).size()];
                for(int i = 0; i < newWeights.length; i++){
                    newWeights[i] = weightsForNodes.get(layer).get(neuron).get(i);
                }


                neurons[layer][neuron] = new Neuron(newWeights,biasesForNodes.get(layer).get(neuron),-50);
            }
        }
    }









    public void training(String dataSetPath){

        String[] files;

    }
    public double cost(double[] expout){
        //base requests based off expected output
        double out=0;
        for (int i = 0; i <outputnodes ; i++) {
            out+=(neurons[layers-1][i].getValue()-expout[i])*(neurons[layers-1][i].getValue()-expout[i]);
        }
        return out;
    }
    public void singlebackprop(double[][][] weights,double[][]bias,double[] expout,int layer,double learningrate){
        weights[layer]=new double[neurons[layer].length][neurons[layer-1].length];
        bias[layer]=new double[neurons[layer].length];
        for (int i = 0; i <outputnodes ; i++) {
            Neuron n=neurons[layer][i];
            double costoverbias = learningrate*(expout[i]-n.getValue());
            bias[layer][i]+=costoverbias;
            for (int j = 0; j <neurons[layer-1].length ; j++) {
                double costoverweight = learningrate* neurons[layer - 1][j].getValue()*(expout[i]-n.getValue());
                weights[layer][i][j] += costoverweight;
            }
        }
    }
    public double sumcost(double[] expout,int layer){
        double val=0;
        for (int i = 0; i <neurons[layer].length ; i++) {
            val+=2*(neurons[layer][i].getValue()-expout[i]);
        }
        return val;
    }
    public void backprop(String[] imgs,Integer[] expectedOutput)throws IOException{
        File f=null;
        BufferedImage img=null;
        double learningrate =2;
        double percent=0;
        double oldpercent=0;
        while ((percent/imgs.length)*100<93){
            double[][][] aggregateWeights=new double[layers][][]; // array full of all requested weight changes
            double[][] aggregateBiases=new double[layers][];
            for (int i = 0; i < imgs.length; i++) {
                //its going to run through a training example find out the weights and nodes that need to be strengthened
                f = new File("data_set/" + imgs[i]);
                try {
                    img = ImageIO.read(f);
                }catch (Exception e){
                    System.out.println(imgs[i]);
                }
                tantum_Numerus_Main.grayScale(img);
                BufferedImage resizedimg=tantum_Numerus_Main.resize(img);
                double[] num = guessthatnumber(resizedimg); //runs teh img through the neural net
                //find what needs to happen
                if((int)num[0]==expectedOutput[i]){
                    percent++;
                }
                double[] out=new double[outputnodes];
                for (int k = 0; k <outputnodes ; k++) {
                    if(k==expectedOutput[i]){
                        out[k]=1;
                    }
                    else {
                        out[k]=0;
                    }
                }
                if(layers==2){
                    singlebackprop(aggregateWeights,aggregateBiases,out,1,learningrate);
                }

            }
            //averages the value of each weight that was requested
            //first list is list of layers second list is list of neurons third list is the list of weights in said neuron

            //finally it will rewrite each neuron to match the average
            for (int i =1;i<aggregateWeights.length;i++) {
                    for (int k=0;k<aggregateWeights[i].length;k++) {
                        for (int l = 0; l <aggregateWeights[i][k].length; l++) {
                            aggregateWeights[i][k][l] /= imgs.length;

                        }
                        aggregateBiases[i][k]/= imgs.length;
                    }
            }
            //takes the averaged list and change neurons accordingly
            for (int i = 1; i <layers ; i++) {
                for (int k = 0; k <neurons[i].length ; k++) {
                    neurons[i][k].changeweights(aggregateWeights[i][k]);
                    neurons[i][k].changebias(aggregateBiases[i][k]);
                }
            }
//            if(oldpercent<(100 * percent / imgs.length)) {
//
//                oldpercent=100 * percent / imgs.length;
//            }
            System.out.println(100 * percent / imgs.length);
            percent=0;
        } //repeat

        try {
            savestate("SavedStates\\results");
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public double[] guessthatnumber(BufferedImage img){
        //reads in the img into the first layer

        int whereWeAt = 0;
        for(int x = 0; x < width; x++){
            for(int y=0; y < height; y++){

                int rgb=img.getRGB(x,y);
                int red = (rgb >> 16) & 0x000000FF;
                int green = (rgb >>8 ) & 0x000000FF;
                int blue = (rgb) & 0x000000FF;
                if (rgb == 765){
                    rgb =0;
                }
                float luminance = (red * 0.2126f + green * 0.7152f + blue * 0.0722f)-128;
                //System.out.println(luminance);
                neurons[0][whereWeAt].setValue(luminance);

                whereWeAt++;
            }
        }

        //loops through the many layers and nuerons updating each layers value
        for (int i = 1;i <neurons.length ; i++) {
            for (int j = 0; j <neurons[i].length ; j++) {
                neurons[i][j].calculateval(neurons[i-1]);
            }
        }
        //finds the hieghest of the out put nodes and returns what it believes teh number is
        int highest=-1;
        Neuron highestnueron=new Neuron(-1000,outputnodes);
        for (int i = 0; i <neurons[layers-1].length ; i++) {
            if(highestnueron.getValue() < neurons[layers-1][i].getValue()){
                highestnueron=neurons[layers-1][i];
                highest=i;
            }
        }
        //returns the number it believes the image to be as well as its certainty
        double[] out ={highest,highestnueron.getValue()*100};
        return out;
    }

}

class Neuron{
    private double value;
    protected double[] weights;
    protected double bias;
    //maybe it should have a list of nodes from previous layer. maybe
    //comment from Owen: Is it necessary to have both weights and biases?
    public Neuron(double val, int previouslayer){

        weights=new double[previouslayer];
        bias=0;
        value=sigmoid(val);
    }
    public void scramble(){
        for (int i = 0; i <weights.length ; i++) {
            weights[i]=(Math.random())-0.5;

        }
        bias=(Math.random());
    }
    //last resort for evolution
    protected void randomMutation(){

        for(int i = 0; i < weights.length; i++){
            weights[i] += (Math.random()/4- .1125);

        }
//
//        for(int i = 0; i < bias.length; i++){
//            bias[i] += (Math.random()- .5);
//        }
    }


    public void changeweights(double [] w){
        for (int i = 0; i <w.length; i++) {
            weights[i]+=w[i];
        }
    }


    public void changebias(double b){
        bias=b;
    }

    //Owen's homemade constructor, for making a neuron when you know about it
    public Neuron(double[] weights, double bias, int val){
        this.weights = weights;
        this.bias = bias;
        value = sigmoid(val);
    }

    public double getValue(){
        return value;
    }
    public void setValue(double newVal){
        value = sigmoid(newVal);
    }


    public double[] requestsweights(double change,Neuron[] previousnodes){ //makes a request for changes to weights given a target value
        double[] request=new double[previousnodes.length];
        for (int i = 0; i <weights.length ; i++) {
            request[i]=weights[i]+change*previousnodes[i].value;
        }
        return request;
    }

    public double[] requestsneurons(double change,Neuron[] previousnodes){//changes the weight of a neuron tahn returns requests for previous layer neurons
        double[] requests=new double[previousnodes.length];
        for (int i = 0; i <previousnodes.length ; i++) {
            requests[i]=sigmoid(previousnodes[i].getValue()+weights[i]*change);
        }
        return requests;
    }
    public void calculateval(Neuron[] neurons){
        double val=0;
        for(int i= 0;i<weights.length;i++){
            val+=neurons[i].value*weights[i];
        }
        value=sigmoid(val+bias);
    }
    public static double sigmoid(double x) {
        return (1/( 1 + Math.pow(Math.E,(-1*x))));
    }

}

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.stream.Stream;




//hello, I changed a thing, you initially spelled "neurons" wrong garcia
public class neural_Net {
    private Neuron[][] neurons;
    private int layers;
    public final int width;
    public final int height;
    public final int outputnodes=10;
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
                    for (int bias = 0; bias < neurons[layer][neuron].bias.length - 1; bias++) {
                        out.write(Double.toString(neurons[layer][neuron].bias[bias]));
                        out.write(" ");
                    }
                    out.write(Double.toString(neurons[layer][neuron].bias[neurons[layer][neuron].bias.length - 1]));
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
        ArrayList<ArrayList<ArrayList<Double>>> biasesForNodes = new ArrayList<>();


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
                    biasesForNodes.get(currentLayer).add(cachedList);
                    //if we get an error here, change it to length-1
                    lineInterpreter.delete(0,lineInterpreter.length());
                    cachedList = new ArrayList<Double>();
           
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
                double[] newBiases = new double[biasesForNodes.get(layer).get(neuron).size()];
                for(int r = 0; r < newBiases.length; r++){
                    newBiases[r] = biasesForNodes.get(layer).get(neuron).get(r);
                }

                neurons[layer][neuron] = new Neuron(newWeights,newBiases,-50);
            }
        }
    }









    public void training(String dataSetPath){

        String[] files;

    }
    public void sumofweights(int expoutput,double[][][] aggregate){
        double[] requests=cost(expoutput);
        for (int i = layers-1; i >1 ; i++) {
            aggregate[i]=new double[neurons[i].length][neurons[i-1].length];
            for (int j = 0; j <neurons[i].length; j++) {
                for (int k = 0; k <neurons[i-1].length ; k++) {
                    aggregate[i][j][k]+=neurons[i][j].requestsweights(requests[j],neurons[i-1])[k];
                }
            }
            requests=neuronchanges(i-1,requests);
        }
    }

    public double[] neuronchanges(int layer,double[] changes){
        //finds what the average requests for neurons of the previuos layer is
        double[] request=new double[neurons[layer].length];
        for (int i = 0; i <neurons[layer+1].length ; i++) {
            for (int j = 0; j <neurons[layer].length ; j++) {
                request[j]+=neurons[layer+1][i].requestsneurons(changes[i],neurons[layer])[j];
            }
        }
        for (int i = 0; i <request.length ; i++) {
            request[i]/=neurons[layer].length;
        }
        //average the requestes made by nuerons of previous layer
        return request;
    }
    public double[] cost(double expout){
        //base requests based off expected output
        double[] out=new double[outputnodes];
        for (int i = 0; i <outputnodes ; i++) {
            if(i==expout){
                out[i]=1-neurons[layers-1][i].getValue();
            }
            else{
                out[i]=neurons[layers-1][i].getValue()-1;
            }
        }
        return out;
    }

    private void backprop(String[] imgs,int[] expectedOutput,int numloops)throws IOException{
        File f=null;
        BufferedImage img=null;
        double[][][] aggregateWeights=new double[layers][][]; // array full of all requested weight changes
        for(int j=0;j<numloops;j++) {
            for (int i = 0; i < imgs.length; i++) {
                //its going to run through a training example find out the weights and nodes that need to be strengthened
                f = new File("Images\\" + imgs[i] + ".png");
                img = ImageIO.read(f);
                guessthatnumber(img); //runs teh img through the neural net
                //find what needs to happen
                sumofweights(expectedOutput[j],aggregateWeights); //propigates backwards through the neural net adding requests to aggregatedWeights
                //for each neuron add to the weights the absolute value some number(amount it ust be changed times the value of the proceeding neuron
                //request decreases in the neurons of the previus layer by multiplying some number by teh weights associated with those neurons


                // call a recursive (maybe normal loop instead) function that repeats the process for each layer

                // then it will store that info
            }
            //averages the value of each weight that was requested
            //first list is list of layers second list is list of neurons third list is the list of weights in said neuron
            for (double[][] layer:aggregateWeights) {
                for (double[] weights:layer) {
                    for (int i = 0; i <weights.length ; i++) {
                        weights[i]/=imgs.length;
                    }
                }

            }
            //takes the averaged list and change neurons accordingly
            for (int i = 0; i <layers ; i++) {
                for (int k = 0; k <neurons[k].length ; k++) {
                    neurons[i][k].changeweights(aggregateWeights[i][k]);
                }
            }
            //finally it will rewrite each neuron to match the average

        } //repeat
        try {
            savestate("results");
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public int[] guessthatnumber(BufferedImage img){
        //reads in the img into the first layer

        int whereWeAt = 0;
        for(int x = 0; x < width; x++){
            for(int y=0; y < height; y++){

                int rgb=img.getRGB(x,y);
                int red = (rgb >> 16) & 0x000000FF;
                int green = (rgb >>8 ) & 0x000000FF;
                int blue = (rgb) & 0x000000FF;

                rgb = (red + green + blue);
                if (rgb == 765){
                    rgb =0;
                }
                neurons[0][whereWeAt].setValue((red + green + blue));

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
        Neuron highestnueron=neurons[layers-1][0];
        for (int i = 0; i <neurons[layers-1].length ; i++) {
            if(highestnueron.getValue() < neurons[layers-1][i].getValue()){
                highestnueron=neurons[layers-1][i];
                highest=i;
            }
        }
        //returns the number it believes the image to be as well as its certainty
        int[] out ={highest,(int)highestnueron.getValue()*100};
        return out;
    }

}

class Neuron{
    private double value;
    protected double[] weights;
    protected double[] bias;
    //maybe it should have a list of nodes from previous layer. maybe
    //comment from Owen: Is it necessary to have both weights and biases?
    public Neuron(double val, int previouslayer){

        weights=new double[previouslayer];
        bias=new double[previouslayer];
        value=sigmoid(val);
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
        weights=w.clone();
    }


    public void changebias(double [] b){
        bias=b.clone();
    }


    //Owen's homemade constructor, for making a neuron when you know about it
    public Neuron(double[] weights, double[] biases, int val){
        this.weights = weights;
        this.bias = biases;
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
            requests[i]=previousnodes[i].value+weights[i]*change;
        }
        return requests;
    }
    public void calculateval(Neuron[] neurons){
        double val=0;
        for(int i= 0;i<weights.length;i++){
            val+=neurons[i].value*weights[i]+bias[i];
        }
        value=sigmoid(val);
    }
    public static double sigmoid(double x) {
        return (1/( 1 + Math.pow(Math.E,(-1*x))));
    }

}

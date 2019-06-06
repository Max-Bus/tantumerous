import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.stream.Stream;




//hello, I changed a thing, you initially spelled "neurons" wrong
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
            neurons[i]=new Neuron[(int)Math.sqrt(neurons[i-1].length)+outputnodes];
            for (int j = 0; j <neurons[i].length ; j++) {
                neurons[i][j]=new Neuron(0,neurons[i-1].length);
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
            for(int neuron = 0; neuron < neurons[0].length; neuron++){

                //these for loops do the output for individual neurons
                for(int weight = 0; weight < neurons[layer][neuron].weights.length-1; weight++){
                    out.write(Double.toString(neurons[layer][neuron].weights[weight]));
                    out.write(" ");
                }
                out.write(Double.toString(neurons[layer][neuron].weights[neurons[layer][neuron].weights.length-1]));
                out.write(':');
                for(int bias = 0; bias < neurons[layer][neuron].bias.length-1; bias++){
                    out.write(Double.toString(neurons[layer][neuron].bias[bias]));
                    out.write(" ");
                }
                out.write(Double.toString(neurons[layer][neuron].bias[neurons[layer][neuron].bias.length-1]));

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
            for(int neuron = 0; neuron < neurons[0].length; neuron++){
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

    private void backprop(){

    }

    public int guessthatnumber(BufferedImage img){
        for(int i=0;i<neurons[0].length;i++){
            neurons[0][i].value=img.getRGB(i/width,i%height);
        }
        for (int i = 1;i <neurons.length ; i++) {
            for (int j = 0; j <neurons[i].length ; j++) {
                neurons[i][j].calculateval(neurons[i-1]);
            }
        }
        int highest=-1;
        Neuron highestnueron=neurons[layers][0];
        for (int i = 0; i <neurons[layers].length ; i++) {
            if(highestnueron.value<neurons[layers][i].value){
                highestnueron=neurons[layers][i];
                highest=i;
            }
        }
        return highest;
    }

}

class Neuron{
    double value;
    double[] weights;
    double[] bias;
    //maybe it should have a list of nodes from previous layer
    //comment from Owen: Is it necessary to have both weights and biases?
    public Neuron(double val, int previouslayer){

        weights=new double[previouslayer];
        bias=new double[previouslayer];
        value=val;
    }

    //last resort for evolution
    protected void randomMutation(){
        for(int i = 0; i < weights.length; i++){
            weights[i] += (Math.random()- .5);
        }
        for(int i = 0; i < bias.length; i++){
            bias[i] += (Math.random()- .5);
        }
    }

    //Owen's homemade constructor, for making a neuron when you know about it
    public Neuron(double[] weights, double[] biases, int val){
        this.weights = weights;
        this.bias = biases;
        value = val;
    }


    public double[] requestsweights(double target){ //makes a request for changes to weights given a target value
        return null;
    }


    //public double[] requestsneurons(double target){} // asks for changes in parent neurons maybe implemented
    public void calculateval(Neuron[] neurons){
        for(int i= 0;i<weights.length;i++){
            value+=neurons[i].value*weights[i]+bias[i];
        }
    }

}

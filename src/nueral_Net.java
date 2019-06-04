import java.awt.image.BufferedImage;

public class nueral_Net {
    public Nueron[][] nuerons;
    public nueral_Net(int layers){

    }
    public void savestate(){

    }
    public void training(/*tests not sure what data type*/){

    }
    private void backprop(){

    }
    public int guessthatnumber(BufferedImage img){
        return -1;
    }

}
class Nueron{
    double value;
    double[] weights;
    double[] bias;
    //maybe it should have a list of nodes from previous layer
    public Nueron(double val, int previouslayer){
        weights=new double[previouslayer];
        bias=new double[previouslayer];
        value=val;
    }
    public double[] requestsweights(double target){ //makes a request for changes to weights given a target value
        return null;
    }
    //public double[] requestsnuerons(double target){} // asks for changes in parent nuerons maybe implemented
    public void calculateval(Nueron[] nuerons){
        for(int i= 0;i<weights.length;i++){
            value+=nuerons[i].value*weights[i]+bias[i];
        }
    }

}

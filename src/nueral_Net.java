import java.awt.image.BufferedImage;

public class nueral_Net {
    private Nueron[][] nuerons;
    private int layers;
    public nueral_Net(int layers){
        this.layers=layers;
        //creates nodes
    }
    public void savestate(){

    }
    public void training(/*tests not sure what data type*/){

    }
    private void backprop(){

    }
    public int guessthatnumber(BufferedImage img){
        for(int i=0;i<nuerons[0].length;i++){
            nuerons[0][i].value=img.getRGB(i%20,i/20);
        }
        for (int i = 1;i <nuerons.length ; i++) {
            for (int j = 0; j <nuerons[i].length ; j++) {
                nuerons[i][j].calculateval(nuerons[i-1]);
            }
        }
        int highest=-1;
        Nueron highestnueron=nuerons[layers][0];
        for (int i = 0; i <nuerons[layers].length ; i++) {
            if(highestnueron.value<nuerons[layers][i].value){
                highestnueron=nuerons[layers][i];
                highest=i;
            }
        }
        return highest;
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

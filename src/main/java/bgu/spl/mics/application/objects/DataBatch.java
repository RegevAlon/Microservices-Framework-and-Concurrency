package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    int StartIndex;
    Data data;
    GPU senderGPU;
    boolean processed;

    public DataBatch(int _index, Data _data){
        StartIndex = _index;
        data = _data;
        processed = false;
    }
    public void setSenderGPU(GPU gpu){
        senderGPU = gpu;
    }
    public Data getData(){return data;}
    public void process(){
        processed = true;
    }


}
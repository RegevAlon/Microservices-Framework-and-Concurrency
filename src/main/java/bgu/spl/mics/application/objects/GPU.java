package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    public enum Type {RTX3090, RTX2080, GTX1080}
    int TotalTrained =0;
    private Type type;
    private Model model;
    boolean isWorkingOnAmodel;
    private ArrayList<DataBatch> processedDatabatch;
    private ArrayList<DataBatch> unProcessedDatabatch;
    int SIZE;
    private static int GPUTime=0;


    public GPU(String _type){
        switch (_type){
            case "RTX3090":
                type = Type.RTX3090;
                SIZE = 32;

                break;
            case "RTX2080":
                SIZE = 16;
                type = Type.RTX2080;
                break;
            case "GTX1080":
                SIZE = 8;
                type = Type.GTX1080;
                break;
        }
        processedDatabatch = new ArrayList<DataBatch>() ;
        unProcessedDatabatch = new ArrayList<DataBatch>();
        isWorkingOnAmodel = false;
    }

    public static int getGPUTime() {
        return GPUTime;
    }

    public Type getType() {
        return type;
    }

    public Model getModel() {
        return model;
    }
    public int getSize() {
        return SIZE;
    }

    public List<DataBatch> getUnProcessed(){return unProcessedDatabatch;}
    public List<DataBatch> getProcessed(){return processedDatabatch;}


    public synchronized void divideToDataBatches(){
        GPUTime++;
        isWorkingOnAmodel=true;
        int dataSize = model.getData().getSize();
        int index = 0;
        while (index<dataSize){
            unProcessedDatabatch.add(new DataBatch(index,model.getData()));
            index+=1000;
        }
    }
    public DataBatch getNextDataBatch(){
        if (unProcessedDatabatch.isEmpty()){
            return null;
        }
        else {
            DataBatch toReturn =  unProcessedDatabatch.get(0);
            unProcessedDatabatch.remove(0);
            return toReturn;
        }
    }

    public int getTotalTrained() {
        return TotalTrained;
    }

    public void trainDataBatches(int Ticks){
        GPUTime++;
        switch (type) {
            case RTX3090:
                model.getData().increment();
                TotalTrained++;
                processedDatabatch.remove(0);
                break;

            case RTX2080:
                if (Ticks % 2 == 0) {
                    model.getData().increment();
                    TotalTrained++;
                    processedDatabatch.remove(0);
                }
                break;

            case GTX1080:
                if (Ticks % 4 == 0) {
                    model.getData().increment();
                    TotalTrained++;
                    processedDatabatch.remove(0);
                }
                break;
        }
    }
    public List<DataBatch> sendDataToProcess(){
        if (processedDatabatch.size() < SIZE) {
            /*DataBatch dataBatch = unProcessedDatabatch.get(0);
            dataBatch.setSenderGPU(this);
            unProcessedDatabatch.remove(unProcessedDatabatch.size() - 1);
            return dataBatch;
        }*/
            int size = Math.min(unProcessedDatabatch.size(), SIZE);
            List<DataBatch> d = new ArrayList<>();
            for(int i=0;i<size;i++){
                d.add(unProcessedDatabatch.get(0));
                unProcessedDatabatch.remove(0);
            }
            for (DataBatch batches : d)
                batches.setSenderGPU(this);
            return d;
        }
        return null;
    }
    public void resetGPU(Model _model){
        this.model =_model;
    }
}
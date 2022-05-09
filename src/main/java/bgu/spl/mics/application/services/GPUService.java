package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.DataBatch;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the { DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    int totalTrained =0;
    private final GPU my_GPU;
    private MessageBusImpl msgBus;
    private Cluster cluster;
    private int tickTime;
    private int tickCount;
    boolean waitingForData = false;
    private ConcurrentLinkedQueue<Model> modelsToTrain;
    private boolean preTrained;


    public GPUService(GPU gpu) {
        super("GPU-Service");
        msgBus = MessageBusImpl.getInstance();
        cluster = Cluster.getInstance();
        my_GPU = gpu;
        tickCount = 0;
        modelsToTrain = new ConcurrentLinkedQueue<>();
        preTrained = true;


    }

    public boolean isWaitingForData() {
        return waitingForData;
    }

    public void setWaitingForData(boolean waitingForData) {
        this.waitingForData = waitingForData;
    }

    public GPU getGPU(){return my_GPU;}

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class,(shut)->{
            System.out.println("TOTAL TRAINED: "+ my_GPU.getTotalTrained());
            terminate();
        });
        subscribeEvent(TrainModelEvent.class,(process)->{
            modelsToTrain.add(process.getModel());

        });
        subscribeBroadcast(TickBroadcast.class,(tick)->{

            if(preTrained){
                if(!modelsToTrain.isEmpty()){

                    my_GPU.resetGPU(modelsToTrain.peek());
                    my_GPU.divideToDataBatches();
                    my_GPU.getModel().updateStatus(1);
                    preTrained = false;
                    System.out.println("GPU: " + this.getName() + " Received model: " + my_GPU.getModel().getName() + Thread.currentThread().getName());
                }
            }
            else {
                if (!my_GPU.getProcessed().isEmpty()){
                    tickCount++;
                    my_GPU.trainDataBatches(tickCount);
                    if (my_GPU.getModel().getData().modelIsDone()){
                        my_GPU.getModel().updateStatus(2);
                        modelsToTrain.poll();
                        preTrained = true;
                    }
                }
                //else {
                if(!waitingForData){
                    List<DataBatch> dataBatch = my_GPU.sendDataToProcess();
                    if (dataBatch != null) {
                        cluster.unProcessDataBatches(this, dataBatch);
                    }
                }
                else waitingForData=false;
                while (!cluster.processedData.get(my_GPU).isEmpty()&&my_GPU.getProcessed().size()<my_GPU.getSize()) {
                    DataBatch processed = cluster.sendBackToGpu(my_GPU);
                    if (processed != null) {
                        my_GPU.getProcessed().add(processed);
                    } else break;
                }
            }


            //}
        });

        subscribeEvent(TestModelEvent.class,(tested)->{
            Random rnd = new Random();
            tested.getModel().updateResults(rnd.nextInt(10));
            tested.getModel().updateStatus(3);
            complete(tested,tested.getModel());
        });

        //System.out.println("TOTAL GPUS: "+Cluster.getInstance().getReceivedFromGPU()+"              "+Thread.currentThread().getName());
        //System.out.println("TOTAL CPUS: "+Cluster.getInstance().getReceivedFromCPU()+"              "+Thread.currentThread().getName());
        // System.out.println("TOTAL Trained DataBatch:                    "+my_GPU.getTotalTrained());

    }
}
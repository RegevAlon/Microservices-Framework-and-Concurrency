package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.DataBatch;

/**
 * CPU service is responsible for handling the { DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    CPU my_CPU;
    MessageBusImpl msgBus;
    Cluster cluster;
    public CPUService(CPU cpu) {
        super("CPU-Service");
        my_CPU = cpu;
        msgBus = MessageBusImpl.getInstance();
        cluster = Cluster.getInstance();

    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class,(shut)->terminate());
        subscribeBroadcast(TickBroadcast.class,(tick)->{
            if(my_CPU.getDataBatchSize() != 0){
                my_CPU.tick();
                DataBatch proccessed = my_CPU.ProcessData();
                if(proccessed != null){
                    cluster.sendProcessDataBatch(proccessed);
                }
            }
            else{
                cluster.pullUnProccessedData(my_CPU);
            }
        });

    }
}
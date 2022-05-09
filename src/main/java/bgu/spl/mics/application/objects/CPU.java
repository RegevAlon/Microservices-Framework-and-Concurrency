package bgu.spl.mics.application.objects;

import java.util.ArrayList;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    static int CPUTime=0;
    private int cores;
    private ArrayList<DataBatch> dataBatch;
    private Cluster cluster;
    private int ticks;
    private boolean isWorking;

    public CPU(int _cores) {
        CPUTime=0;
        cores = _cores;
        cluster = Cluster.getInstance();
        isWorking = false;
        dataBatch = new ArrayList<>();
    }

    public static int getCPUTime() {
        return CPUTime;
    }

    public int getCores(){return cores;}
    public int getDataBatchSize(){return dataBatch.size();}

    public void ReceiveDataBatch(ArrayList<DataBatch> d) {
        dataBatch.addAll(d);
    }

    public DataBatch ProcessData(){
        isWorking = true;
        if (!dataBatch.isEmpty()) {
            DataBatch d = dataBatch.get(0);
            int t=0;
            switch (d.getData().getType()) {
                case Images:
                    t=4;
                case Text:
                    t=2;
                case Tabular:
                    t=1;
            }
            if (ticks >= (32 / cores) * t) {
                ticks = 0;
                d.process();
                dataBatch.remove(0);
                return d;
            }
        }
        return null;
    }

    public void tick() {
        if (isWorking) {
            CPUTime++;
            ticks++;
        }
    }
}
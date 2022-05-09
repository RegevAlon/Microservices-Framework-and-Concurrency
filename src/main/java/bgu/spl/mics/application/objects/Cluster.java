package bgu.spl.mics.application.objects;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.services.CPUService;
import bgu.spl.mics.application.services.GPUService;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	private static class ClusterHolder{
		private static Cluster instance=new Cluster();
	}
	public static Cluster getInstance() {
		return ClusterHolder.instance;
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public HashMap<GPU, ArrayList<DataBatch>> processedData;
	public HashMap<GPU, ArrayList<DataBatch>> unProcessedData;
	public PriorityBlockingQueue<GPU>  totalProcessedData;
	//public ConcurrentLinkedQueue<GPU> totalProcessedData;
	private ValidatedMap statistics;
	private int dataCounter;
	private int gpuTime;
	private int cpuTime;
	private ArrayList<GPU> GPUS;
	//private PriorityBlockingQueue<CPU> CPUS;
	private Vector<CPU> CPUS;
	int receivedFromCPU;
	int receivedFromGPU;


	private Cluster() {
		statistics = new ValidatedMap();
		statistics.put("Models", " ");
		statistics.put("dataProcessed", dataCounter);
		statistics.put("GPUTime", gpuTime);
		statistics.put("CPUTime", cpuTime);
		GPUS = new ArrayList<>();
		//totalProcessedData = new ConcurrentLinkedQueue<>();
		totalProcessedData = new PriorityBlockingQueue<GPU>(20, Comparator.comparingInt(a -> a.getProcessed().size()));
		//CPUS = new PriorityBlockingQueue<CPU>(20,(a, b)->Integer.compare(a.getDataBatchSize()/a.getCores(), b.getDataBatchSize())/b.getCores());
		CPUS = new Vector<>();
		processedData = new HashMap<GPU, ArrayList<DataBatch>>();
		unProcessedData = new HashMap<GPU, ArrayList<DataBatch>>();


		cpuTime = 0;
		gpuTime = 0;
		dataCounter = 0;
		receivedFromCPU = 0;
		receivedFromGPU = 0;

	}

	public HashMap<GPU, ArrayList<DataBatch>> getProcessedData() {
		return processedData;
	}

	public ValidatedMap getStatistics() {
		return statistics;
	}

	public  void setReceivedFromCPU(int receivedFromCPU) {
		this.receivedFromCPU+=receivedFromCPU;
	}

	public  void setReceivedFromGPU(int receivedFromGPU) {
		this.receivedFromGPU+=receivedFromGPU;
	}

	public void pullUnProccessedData(CPU cpu) {
		if (!GPUS.isEmpty()) {
			GPU gpu = GPUS.get(0);
			GPUS.remove(gpu);
			addGPU(gpu);
			if (!unProcessedData.get(gpu).isEmpty()){
				ArrayList<DataBatch> db = unProcessedData.get(gpu);
				if (db != null) {
					cpu.ReceiveDataBatch(db);
					unProcessedData.get(gpu).remove(db);
				}
			}
		}
	}

	public void unProcessDataBatches(GPUService m, List<DataBatch> dataBatch) {
		if (!unProcessedData.containsKey(m.getGPU())) {
			addGPU(m.getGPU());
			processedData.put(m.getGPU(), new ArrayList<DataBatch>());
			unProcessedData.put(m.getGPU(), new ArrayList<DataBatch>());
			totalProcessedData.add(m.getGPU());
		}
		receivedFromGPU+=dataBatch.size();
		statistics.replace("dataProcessed", dataCounter, dataCounter+dataBatch.size());
		unProcessedData.get(m.getGPU()).addAll(dataBatch);
	}

	public int getReceivedFromCPU() {
		return receivedFromCPU;
	}

	public synchronized int getReceivedFromGPU() {
		return receivedFromGPU;
	}

	public synchronized void sendProcessDataBatch(DataBatch dataBatch) {
		receivedFromCPU+=1;
		processedData.get(dataBatch.senderGPU).add(dataBatch);
		totalProcessedData.add(dataBatch.senderGPU);

	}

	public DataBatch sendBackToGpu(GPU gpu){
		if (processedData.containsKey(gpu)){
			if (!processedData.get(gpu).isEmpty()){
				DataBatch databatch =processedData.get(gpu).get(0);
				processedData.get(gpu).remove(0);
				return databatch;
			}

		}
		return null;
	}

	public void Tick(MicroService m) {
		try {
			if (m.getClass() == CPUService.class) {
				cpuTime++;
				statistics.replace("CPUTime", cpuTime, cpuTime++);
			}
			if (m.getClass() == GPUService.class) {
				gpuTime++;
				statistics.replace("GPUTime", gpuTime, gpuTime++);
			}
		} catch (IllegalArgumentException e) { }
	}
	public void addCPU(CPU cpu){
		CPUS.add(cpu);
	}
	public void addGPU(GPU gpu){
		GPUS.add(gpu);
	}

}
package bgu.spl.mics;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static class MessageBussHolder{
		private static MessageBusImpl instance=new MessageBusImpl();
	}
	public static MessageBusImpl getInstance() {
		return MessageBussHolder.instance;
	}
	private ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>> microServiceMessages;
	private ConcurrentHashMap<Class<? extends Message>,ConcurrentLinkedQueue<MicroService>> subscriptions;
	private ConcurrentHashMap<Message,Future> status;
	private Object LockService = new Object();
	private Object lockSendEvent = new Object();
	private Object lockSendBroad = new Object();
	private Object lockSubscribe = new Object();


	private MessageBusImpl(){
		microServiceMessages = new ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>>();
		subscriptions=new ConcurrentHashMap<Class<? extends Message>,ConcurrentLinkedQueue<MicroService>>();
		status=new ConcurrentHashMap<Message,Future>();
	}
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if(subscriptions.get(type)==null)
			subscriptions.put(type,new ConcurrentLinkedQueue<MicroService>());
		subscriptions.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if(!subscriptions.containsKey(type)){
			subscriptions.put(type,new ConcurrentLinkedQueue<MicroService>());
		}
		subscriptions.get(type).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		status.get(e).resolve(result);

	}

	@Override
	public synchronized void sendBroadcast(Broadcast b) {
		if (!subscriptions.isEmpty()){
			if(subscriptions.containsKey(b.getClass())){
				for (MicroService m : subscriptions.get(b.getClass())){
					microServiceMessages.get(m).add(b);
				}
				synchronized (LockService){
					LockService.notifyAll();
				}
			}
		}
		notifyAll();
	}

	private synchronized MicroService roundRobin(Class<? extends Event> type){
		if(!subscriptions.isEmpty()){
			while (subscriptions.get(type) == null) {
				try {
					{
						wait();
					}
				}catch (InterruptedException e){}
				}
			if (!subscriptions.get(type).isEmpty()) {
				MicroService microService = subscriptions.get(type).poll();
				subscriptions.get(type).add(microService);
				return microService;
			}
		}
		return null;
	}



	@Override
	public synchronized  <T> Future<T> sendEvent(Event<T> e) {
		MicroService microService = roundRobin(e.getClass());
		if(microService!=null) {
			microServiceMessages.get(microService).add(e);
			Future<T> f = new Future<>();
			status.put(e, f);
			notifyAll();
			synchronized (LockService){
				LockService.notifyAll();
			}
			return f;
		}
		else {
			return  null;

		}
	}

	@Override
	public void register(MicroService m) {
		microServiceMessages.put(m,new ConcurrentLinkedQueue<Message>());
	}

	@Override
	public void unregister(MicroService m) {
		microServiceMessages.remove(m);

	}
	@Override
	public Message awaitMessage(MicroService m)  {
		Queue<Message> q = microServiceMessages.get(m);

		try{
			synchronized (LockService) {
				while (q.isEmpty()) {
					LockService.wait();
				}
			}
		}catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return q.poll();
	}
	@Override
	/*
	public Message awaitMessage(MicroService m){
		if(microServiceMessages.get(m)!=null) {
			try {
				while (microServiceMessages.get(m).isEmpty()) {
					wait();
				}
		}catch (InterruptedException e){};

		Message message = microServiceMessages.get(m).poll();
		return  message;
		}
		return null;

	}*/

	public boolean msgQIsempty(){
		return microServiceMessages.isEmpty();
	}
	public boolean subsIsempty(){
		return subscriptions.isEmpty();
	}
	public boolean statIsempty(){
		return subscriptions.isEmpty();
	}

}
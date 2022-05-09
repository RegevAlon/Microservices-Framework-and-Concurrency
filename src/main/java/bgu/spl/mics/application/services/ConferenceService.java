package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;

import java.util.ArrayList;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConferenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {

    private ArrayList<Model> models;
    private int tickCount;
    private ConfrenceInformation conference;

    public ConferenceService(ConfrenceInformation _conference) {
        super("ConferenceService");
        tickCount = 0;
        models = new ArrayList<>();
        conference = _conference;
    }
    public void tick(){
        tickCount++;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class,(shut)->terminate());
        subscribeEvent(PublishResultsEvent.class,(model)->{
            models.add(model.getModel());
        });
        subscribeBroadcast(TickBroadcast.class,(tick)->{
            tick();
            if(tickCount == conference.getDate()){
                sendBroadcast(new PublishConferenceBroadcast(models));
                //unregister();
                //terminate();
            }
        });

    }
}

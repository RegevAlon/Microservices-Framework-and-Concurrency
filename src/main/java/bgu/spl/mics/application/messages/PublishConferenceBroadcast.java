package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.ArrayList;

public class PublishConferenceBroadcast implements Broadcast {
    ArrayList<Model> models;
    public PublishConferenceBroadcast(ArrayList<Model> _models){
        models = _models;
    }
    public ArrayList<Model> getModels(){
        return models;
    }
}

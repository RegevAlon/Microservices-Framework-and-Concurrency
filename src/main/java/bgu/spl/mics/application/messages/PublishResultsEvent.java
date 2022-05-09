package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class PublishResultsEvent implements Event<Model> {
    public Model model;

    public PublishResultsEvent(Model _model){
        this.model = _model;
    }
    public Model getModel() {
        return model;
    }
}

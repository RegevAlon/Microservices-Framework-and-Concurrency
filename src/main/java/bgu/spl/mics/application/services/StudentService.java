package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.ArrayList;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    private Student student;
    private ArrayList<Model> models;
    private int testedModels_num;
    private int trainedModels;
    private String status;

    public StudentService(Student student,ArrayList<Model> models){
        super(student.getName());
        this.student = student;
        this.models = models;
        testedModels_num = models.size();
        trainedModels = 0;
        status = "Training";

    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class, (bool) -> terminate());

        sendEvent(new TrainModelEvent(student,student.getModels().get(trainedModels)));

        System.out.println(this.getName() + " Sends Model: " + models.get(trainedModels).getName());

        subscribeBroadcast(PublishConferenceBroadcast.class,(publishedModels)->{
            ArrayList<Model> published = publishedModels.getModels();
            for (int i = 0 ; i <published.size();i++){
                if(models.contains(published.get(i))) {
                    student.selfPublished();
                    System.out.println("student read papers");
                }
                else {
                    System.out.println("student read papers");
                    student.readPapers();
                }
            }
        });
        subscribeBroadcast(TickBroadcast.class,(tick)->{

            if (status == "Training"){
                Model m = student.getModels().get(trainedModels);
                if (m.getStatus() == "Trained") {
                    System.out.println(models.get(trainedModels).getName() + "Model Finished");
                    sendEvent(new TestModelEvent(student, m));
                    status = "Trained";
                }
            }
            if (status == "Trained") {
                Model m = student.getModels().get(trainedModels);
                if (m.getResults() == "Good") {
                    sendEvent(new PublishResultsEvent(m));
                    //System.out.println(models.get(trainedModels).getName() + "Model Finished");
                    status = "Tested";

                }
                if (m.getResults() == "Bad") {
                    //System.out.println(models.get(trainedModels).getName() + "Model Finished");
                    status = "Tested";

                }
            }
            if (status == "Tested") {
                trainedModels++;
                if (trainedModels < student.getModels().size()) {
                    System.out.println(this.getName() + " Sends Model: " + models.get(trainedModels).getName());
                    sendEvent(new TrainModelEvent(student, student.getModels().get(trainedModels)));

                    status = "Training";
                } else {
                    status = "Finished";
                }
            }
            });
    }
}
/*
for (Model model : models) {
            sendEvent(new TrainModelEvent(student, model));
            System.out.println(this.getName() + " Sends Model: " + model.getName());
            try {
                while (model.getStatus() != "Tested") {
                    Thread.sleep(50*System.currentTimeMillis());
                }
            } catch (InterruptedException e) {}
            sendEvent(new TestModelEvent(student, model));
            try {
                while (model.getResults() == null) {
                    Thread.sleep(50*System.currentTimeMillis());
                }
            } catch (InterruptedException e) {}
            student.getPapersRead();
            switch (model.getStatus()) {
                case "Good":
                    sendEvent(new PublishResultsEvent(model.getName()));
                    break;
                case "Bad":
                    break;

 */
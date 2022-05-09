package bgu.spl.mics.application;

import bgu.spl.mics.JsonParse;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.Output;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */

public class CRMSRunner {

    public static void main(String[] args) throws IOException {
        MessageBusImpl msgBus = MessageBusImpl.getInstance();
        Cluster cluster = Cluster.getInstance();
        Gson gson = new Gson();
        Reader reader = new FileReader("example_input.json");
        JsonParse input = gson.fromJson(reader, JsonParse.class);
        ArrayList<CPU> CPUS = new ArrayList<>();
        ArrayList<GPU> GPUS = new ArrayList<>();
        ArrayList<ConfrenceInformation> Conferences = new ArrayList<>();
        ArrayList<Student> Students = new ArrayList<>();
        ArrayList<Thread> CPUThreads = new ArrayList<>();
        ArrayList<Thread> GPUThreads = new ArrayList<>();
        ArrayList<Thread> StudentThreads = new ArrayList<>();
        ArrayList<Thread> ConferencesThreads = new ArrayList<>();

        for (int i = 0; i < input.getStudents().size(); i++) {
            ArrayList<Model> models = new ArrayList<>();
            for (int j = 0; j < input.getStudents().get(i).getModels().size(); j++) {
                Model m = input.getStudents().get(i).getModels().get(j);
                models.add(new Model(m.getName(), m.getType(), m.getSize()));
            }
            Student s = new Student(input.getStudents().get(i).getName(),
                    input.getStudents().get(i).getDepartment(), input.getStudents().get(i).getStatus().toString(),
                    models);
            for (Model model : s.getModels())
                model.setStudent(s);
            Students.add(s);
            StudentThreads.add(new Thread(new StudentService(Students.get(i),models)));
        }
        for (int i = 0; i < input.getCPUS().size(); i++) {
            CPU c = new CPU(input.getCPUS().get(i));
            CPUS.add(c);
            CPUThreads.add(new Thread(new CPUService(CPUS.get(i))));
            cluster.addCPU(c);
        }
        for (int i = 0; i < input.getGPUS().size(); i++) {
            GPU g = new GPU(input.getGPUS().get(i));
            GPUS.add(g);
            GPUThreads.add(new Thread(new GPUService(GPUS.get(i))));
        }
        for(int i=0;i<input.getConferences().size();i++){
            ConfrenceInformation c = input.getConferences().get(i);
            Conferences.add(new ConfrenceInformation(c.getName(),c.getDate()));
            ConferencesThreads.add(new Thread(new ConferenceService(Conferences.get(i))));
        }
        TimeService timeService = new TimeService(1, input.getDuration());

        /*Start Loops:*/

        for (int i = 0; i < StudentThreads.size(); i++) {
            StudentThreads.get(i).start();
        }
        for (int i = 0; i < GPUThreads.size(); i++) {
            GPUThreads.get(i).start();
        }
        for (int i = 0; i < CPUThreads.size(); i++) {
            CPUThreads.get(i).start();
        }
        for(int i=0;i<ConferencesThreads.size();i++){
            ConferencesThreads.get(i).start();
        }
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread timeThread = new Thread(timeService);
        timeThread.start();
        /*Join Loops:*/
        try {
            for (int i = 0; i < StudentThreads.size(); i++) {
                StudentThreads.get(i).join();
            }
            for (int i = 0; i < GPUThreads.size(); i++) {
                GPUThreads.get(i).join();
            }
            for (int i = 0; i < CPUThreads.size(); i++) {
                CPUThreads.get(i).join();
            }
            for(int i=0;i<ConferencesThreads.size();i++){
                ConferencesThreads.get(i).join();
            }
            timeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < Students.size(); i++) {
            System.out.println(Students.get(i).getPapersRead());
        }

        /*----------------OUTPUT------------------*/
        for (Student s: Students)
            for(Model m: s.getModels())
                m.setStudent(null);
        try {
            Gson gson1 = new GsonBuilder().setPrettyPrinting().create();
            Writer writer = new FileWriter("output.json");
            gson1.toJson(new Output(Students, Conferences, CPU.getCPUTime(), GPU.getGPUTime(), cluster.getReceivedFromCPU()), writer);
            writer.flush(); //flush data to file
            writer.close(); //close write
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
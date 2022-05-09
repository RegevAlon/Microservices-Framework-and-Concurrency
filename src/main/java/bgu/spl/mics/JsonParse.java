package bgu.spl.mics;

import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Student;

import java.util.List;

public class JsonParse {
    private List<Student> Students;
    private List<String> GPUS;
    private List<Integer> CPUS;
    private List<ConfrenceInformation> Conferences;
    private int TickTime;
    private int Duration;

    public List<Student> getStudents() {
        return Students;
    }

    public List<String> getGPUS() {
        return GPUS;
    }

    public List<Integer> getCPUS() {
        return CPUS;
    }

    public List<ConfrenceInformation> getConferences() {
        return Conferences;
    }

    public int getTickTime() {
        return TickTime;
    }

    public int getDuration() {
        return Duration;
    }
}
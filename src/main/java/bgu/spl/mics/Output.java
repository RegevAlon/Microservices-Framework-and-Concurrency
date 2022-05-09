package bgu.spl.mics;

import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Student;

import java.util.ArrayList;
import java.util.List;

public class Output {
    private List<Student> Students;
    private List<ConfrenceInformation> Conferences;
    private int CPUTimeUsed;
    private int GPUTimeUsed;
    private int AmountOfProcessedDataBatch;

    public Output(ArrayList<Student> students, List<ConfrenceInformation> conferences, int CPUTimeUsed, int GPUTimeUsed, int AmountOfProcessedDataBatch) {
        Students = students;
        Conferences = conferences;
        this.CPUTimeUsed = CPUTimeUsed;
        this.GPUTimeUsed = GPUTimeUsed;
        this.AmountOfProcessedDataBatch = AmountOfProcessedDataBatch;
    }
}
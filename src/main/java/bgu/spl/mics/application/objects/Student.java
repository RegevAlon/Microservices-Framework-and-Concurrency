package bgu.spl.mics.application.objects;

import java.util.ArrayList;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {

    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MsC, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private ArrayList<Model> models;
    private int publications;
    private int papersRead;

    public Student(String _name, String _department, String _status,ArrayList<Model> _models){
        name = _name;
        department = _department;
        models = _models;
        switch (_status){
            case "MsC":
                status = Degree.MsC;
                break;
            case "PhD":
                status = Degree.PhD;

                break;
        }
    }
    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public String getStatus() {
        return status.toString();
    }

    public int getPublications() {
        return publications;
    }
    public ArrayList<Model> getModels(){return models;}
    public int getPapersRead() {
        return papersRead;
    }
    public void readPapers(){
        papersRead++;
    }
    public void selfPublished(){
        publications++;
    }
}
package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {

    enum Status {
        PreTrained, Training, Trained, Tested
    }

    enum Results {
        None, Good, Bad
    }

    private String name;
    private String type;
    private int size;
    private Data data;
    private Student student;
    private Status status;
    private Results results;


    public Model(String _name, String type, int size){
        name =_name;
        this.type = type;
        this.size = size;
        data = new Data(type,size);
        //student = _student;
        status = Status.PreTrained;
        results = Results.None;
    }
    public String getType(){return type;}
    public int getSize(){return size;}
    public String getName() {
        return name;
    }

    public Data getData() {
        return data;
    }

    //public Student getStudent() {
    //  return student;
    //}

    public String getStatus() {
        return status.toString();
    }

    public String getResults() {
        return results.toString();
    }
    public void setStudent(Student s){
        student = s;
    }
    public void updateStatus(int state) {
        switch (state) {
            case 1:
                status = Status.Training;
                break;

            case 2:
                status = Status.Trained;
                break;

            case 3:
                status = Status.Tested;
                break;

            case 4:
                break;

        }
    }
    public void updateResults(int poll) {
        switch (student.getStatus()) {
            case ("MsC"):
                if (poll <=6){
                    results = Results.Good;
                }
                else {
                    results = Results.Bad;
                }
                break;

            case ("PhD"):
                if (poll <=8){
                    results = Results.Good;
                }
                else {
                    results = Results.Bad;
                }
                break;


        }
    }
}
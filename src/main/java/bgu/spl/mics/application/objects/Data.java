package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    public enum Type {
        Images, Text, Tabular
    }

    private Type type;
    private int processed;
    private int size;
    public Data(String _type, int _size){
        size = _size;
        switch (_type){
            case "Images":
                type = Type.Images;
                break;
            case "Text":
                type = Type.Text;

                break;
            case "Tabular":
                type = Type.Tabular;
                break;
        }
    }
    public Type getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    public int getProcessed() {
        return processed;
    }
    public boolean modelIsDone(){
        return  (size<=processed);
    }
    public void increment(){
        processed += 1000;
    }
}
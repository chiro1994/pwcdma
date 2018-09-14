package pl.chiro.pwcdma;

/**
 * Created by Chiro on 14.09.2018.
 */

public class ToDoList {

    private String id;
    private String listName;
    private String listOwner;
    private boolean isDone;

    public ToDoList() {}

    public ToDoList(String listName, String listOwner) {
        this.listName = listName;
        this.listOwner = listOwner;
    }

    public ToDoList(String listName, boolean isBought) {
        this.listName = listName;
        this.isDone = isBought;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getListOwner() {
        return listOwner;
    }

    public void setListOwner(String listOwner) {
        this.listOwner = listOwner;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
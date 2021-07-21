package ca.nait.zli.lab02todoornot;

public class TodoItem
{
    private int id;
    private int todoNameId;
    private String content;
    private String dateCreated;
    private String completeFlag;

    public TodoItem(int id, int todoNameId, String content, String dateCreated, String completeFlag)
    {
        this.id = id;
        this.todoNameId = todoNameId;
        this.content = content;
        this.dateCreated = dateCreated;
        this.completeFlag = completeFlag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTodoNameId() {
        return todoNameId;
    }

    public void setTodoNameId(int todoNameId) {
        this.todoNameId = todoNameId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getCompleteFlag() {
        return completeFlag;
    }

    public void setCompleteFlag(String completeFlag) {
        this.completeFlag = completeFlag;
    }
}

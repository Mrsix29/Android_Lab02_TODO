package ca.nait.zli.lab02todoornot;

public class TodoName
{
    private int id;
    private String todoName;

    public TodoName(int id, String todoName){
        this.id = id;
        this.todoName = todoName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTodoName()
    {
        return todoName;
    }
    public void setTodoName(String todoName)
    {
        this.todoName = todoName;
    }
}

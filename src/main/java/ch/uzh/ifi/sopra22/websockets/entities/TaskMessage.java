package ch.uzh.ifi.sopra22.websockets.entities;

import ch.uzh.ifi.sopra22.websockets.entities.enums.TaskAction;

public class TaskMessage {
    private Long taskID;
    private String user;
    private TaskAction action;

    public TaskMessage(Long taskID, String user, TaskAction action) {
        this.taskID = taskID;
        this.user = user;
        this.action = action;
    }

    public Long getTaskID() {
        return taskID;
    }

    public void setTaskID(Long taskID) {
        this.taskID = taskID;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public TaskAction getAction() {
        return action;
    }

    public void setAction(TaskAction action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "TaskMessage{" +
                "taskID=" + taskID +
                ", user='" + user + '\'' +
                ", action=" + action +
                '}';
    }
}

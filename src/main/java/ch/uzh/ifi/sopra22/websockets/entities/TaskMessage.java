package ch.uzh.ifi.sopra22.websockets.entities;

import ch.uzh.ifi.sopra22.websockets.entities.enums.TaskAction;

public class TaskMessage {
    private Long taskID;
    private Long userID;
    private Long columnID;
    private TaskAction action;

    public TaskMessage(Long taskID, Long userID, Long columnID, TaskAction action) {
        this.taskID = taskID;
        this.userID = userID;
        this.columnID = columnID;
        this.action = action;
    }

    public Long getTaskID() {
        return taskID;
    }

    public void setTaskID(Long taskID) {
        this.taskID = taskID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Long getColumnID() {
        return columnID;
    }

    public void setColumnID(Long columnID) {
        this.columnID = columnID;
    }

    public TaskAction getAction() {
        return action;
    }

    public void setAction(TaskAction action) {
        this.action = action;
    }
}

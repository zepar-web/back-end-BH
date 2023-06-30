package com.bureaucracyhacks.refactorip.exceptions;

public class TaskAlreadyAssignedException extends RuntimeException{
    public TaskAlreadyAssignedException() {
        super("Task Already Assigned !");
    }
}

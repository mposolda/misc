package org.mposolda.drools.hw;

/**
 * Hello world!
 *
 */
public class Message {
    private String type;
    private int messageValue;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMessageValue() {
        return messageValue;
    }

    public void setMessageValue(int messageValue) {
        this.messageValue = messageValue;
    }

    public void printMessage() {
        System.out.println("Type: " + type + ", messageValue: " + messageValue);
    }

}

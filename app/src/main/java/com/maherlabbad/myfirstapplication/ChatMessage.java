package com.maherlabbad.myfirstapplication;

import java.io.Serializable;
import java.util.Date;

public class ChatMessage implements Serializable {
    public String senderId,receiverId,message,Datetime;
    public Date dateObject;

    public boolean isseen;
}

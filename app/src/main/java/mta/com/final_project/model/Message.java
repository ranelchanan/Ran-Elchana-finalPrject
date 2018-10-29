package mta.com.final_project.model;

import java.util.Calendar;
import java.util.Date;
import java.sql.Time;

public class Message {

    private String MessageId ;
    private String fromUser;
    private String ToUser;

    private String Title;

    private boolean isRead;
    private String itemType;
    private String itemKey;
    private String Text;
    private Date sendDate;
    private  String PrevText;
    public Date getSendDate() {
        return sendDate;
    }

    public String getFromUser() {
        return fromUser;
    }

    public String getText() {
        return Text;
    }

    public String getToUser() {
        return ToUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public void setText(String text) {
        Text = text;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public void setToUser(String toUser) {
        ToUser = toUser;
    }

    public String getItemKey() {
        return itemKey;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getMessageId() {
        return MessageId;
    }

    public void setMessageId(String itemId) {
        this.MessageId = itemId;
    }

    public String getPrevText() {
        return PrevText;
    }

    public void setPrevText(String prevText) {
        PrevText = prevText;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public Message()
    {

    }

    public Message(Message other)
    {
        fromUser = other.getToUser();
        ToUser = other.getFromUser();
        Title = other.getTitle()!=null ? "re :" + other.getTitle(): "Replay to your message";
        itemType = other.getItemType();
        itemKey = other.getItemKey();
        PrevText = other.createPrevText();
        sendDate = Calendar.getInstance().getTime();

    }

    private String  createPrevText()
    {
        String ToCreate="";
        if (PrevText!=null && !PrevText.isEmpty())
        {
            ToCreate = "\n***********************\n" + PrevText;
        }
        return Text + ToCreate;
    }

}

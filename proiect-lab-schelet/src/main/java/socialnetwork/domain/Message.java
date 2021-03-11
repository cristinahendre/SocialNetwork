package socialnetwork.domain;

import sun.nio.ch.Util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Message extends Entity<Long>{
    private Utilizator from;
    private List<Utilizator> to;
    private String message;
    private LocalDateTime date;
    long reply;

    public long getReply() {
        return reply;
    }

    public void setReply(long reply) {

        this.reply=reply;
    }

    @Override
    public String toString() {
        String toAsString="";
        for(Utilizator u:to){

            toAsString+=u.toString()+" ";
        }

            return "Message{" +
                    " id = "+ this.getId()+
                    "  from='" + from + '\'' +
                    ", to=" + toAsString +
                    ", message='" + message + '\'' +
                    ", date=" + date +
                    " reply= " + reply +
                    '}';
        }



    public Utilizator getFrom() {
        return from;
    }

    public void setFrom(Utilizator from) {
        this.from = from;
    }

    public List<Utilizator> getTo() {
        return to;
    }

    public String getToStrings(){
        StringBuilder aux= new StringBuilder();
        for(Utilizator u: to){
            aux.append(u.getFirstName()).append(" ").append(u.getLastName()).append("; ");
        }
        return aux.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message1 = (Message) o;
        return getFrom().equals(message1.getFrom()) &&
                getTo().equals(message1.getTo()) &&
                getMessage().equals(message1.getMessage()) &&
                getDate().equals(message1.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFrom(), getTo(), getMessage(), getDate());
    }

    public void setTo(List<Utilizator> to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Message(Utilizator from, List<Utilizator> to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
    }

    public String getUserFrom(){
        return from.getFirstName()+" "+from.getLastName() ;
    }



}
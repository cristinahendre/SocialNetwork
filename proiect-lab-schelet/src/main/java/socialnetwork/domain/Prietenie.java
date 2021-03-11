package socialnetwork.domain;

//import sun.nio.ch.Util;

import java.time.LocalDateTime;


public class Prietenie extends Entity<Tuple<Long,Long>> {

    LocalDateTime date;




    public Prietenie() {

    }

    public Long getU1(){
        return this.getId().getLeft();
    }


    public Long getU2(){
        return this.getId().getRight();
    }


    @Override
    public String toString() {
        return "Prietenie{" +
                "date=" + date +
                ", u1=" + this.getId().getLeft() +
                ", u2=" + this.getId().getRight() +
                '}';
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }
}

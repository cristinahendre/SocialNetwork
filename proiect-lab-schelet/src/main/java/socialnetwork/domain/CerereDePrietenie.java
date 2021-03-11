package socialnetwork.domain;

//import sun.nio.ch.Util;

import java.time.LocalDateTime;


public class CerereDePrietenie extends Entity<Long> {

    String status; //pending, approved, rejected
    Utilizator trimite;

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    Utilizator primeste;
    LocalDateTime data;



    public CerereDePrietenie(Utilizator trimite, Utilizator primeste) {

        this.trimite=trimite;
        this.primeste=primeste;

    }

    /**
     *
     * @return statusul cererii de prietenie
     */
    public String getStatus() {
        return status;
    }

    /**
     * seteaza un nou status cererii de prietenie
     * @param status ->noul status: poate fi: pending, approved, rejected
     */
    public void setStatus(String status) {
        if(status.equals("pending") || status.equals("approved")|| status.equals("rejected"))
            this.status = status;
    }

    public String getNameTrimite(){
        return trimite.getFirstName()+" "+trimite.getLastName();
    }

    public String getNamePrimeste(){
        return primeste.getFirstName()+" "+primeste.getLastName();
    }


    public Utilizator getTrimite() {
        return trimite;
    }

    public void setTrimite(Utilizator trimite) {
        this.trimite = trimite;
    }

    public Utilizator getPrimeste() {
        return primeste;
    }

    public void setPrimeste(Utilizator primeste) {
        this.primeste = primeste;
    }

    @Override
    public String toString() {
        return "Cerere De Prietenie{" +
                "id = "+this.getId()+
                ", trimite u1=" + this.trimite.toString() +
                ", catre u2=" + this.primeste.toString() +
                " status=" + status +
                " data = "+ data+
                '}';
    }



}

package socialnetwork.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Notificare extends  Entity<Long>{

    Eveniment event;
    String descriere;
    LocalDateTime data;
    int repeat;

    //daca am afisat deja un mesaj intr-o zi =>repeat =1, altfel =0
    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Notificare{" +
                "event=" + event +
                ", descriere='" + descriere + '\'' +
                ", data=" + data +
                ", catre=" + catre +
                " , repeat "+repeat+
                '}';
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public Notificare(Eveniment event, Utilizator catre) {
        this.event = event;
        this.catre = catre;
    }



    public Eveniment getEvent() {
        return event;
    }

    public String getNumeEvent(){
        return  event.nume;
    }



    public String getMesaj(){
        return this.descriere;
    }

    public void setEvent(Eveniment event) {
        this.event = event;
    }

    public Utilizator getCatre() {
        return catre;
    }

    public void setCatre(Utilizator catre) {
        this.catre = catre;
    }

    Utilizator catre;
}

package socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Eveniment  extends  Entity<Long>{

    private Utilizator organizator;
    private List<Utilizator> participanti;
    LocalDateTime data;
    String nume;
    String descriere;

    public String getOrg(){
        return organizator.getFirstName()+" "+organizator.getLastName();
    }

    @Override
    public String toString() {
        return "Eveniment{" +
                " id = "+this.getId()+
                " nume ="+ nume+
                " organizator=" + this.getOrg()+
                ", participanti=" + getParticipantiString() +
                ", data=" + data +
                ", descriere="+descriere+
                '}';
    }

    public String  getParticipantiString(){
        if(participanti == null) return "";
        String rez="";
        for(Utilizator u: participanti){
            rez+=u.getFirstName()+" "+u.getLastName()+";";
        }
        return rez;
    }


    public String  getParticipantiAfisare(){
        if(participanti == null) return "";
        String rez="";
        for(Utilizator u: participanti){
            rez+=u.getFirstName()+" "+u.getLastName()+"\n";
        }
        return rez;
    }


    public Utilizator getOrganizator() {
        return organizator;
    }

    public void setOrganizator(Utilizator organizator) {
        this.organizator = organizator;
    }

    public List<Utilizator> getParticipanti() {
        return participanti;
    }

    public void setParticipanti(List<Utilizator> participanti) {
        this.participanti = participanti;
    }

    public LocalDateTime getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Eveniment)) return false;
        Eveniment eveniment = (Eveniment) o;
        return getOrganizator().equals(eveniment.getOrganizator()) &&
                getData().equals(eveniment.getData()) &&
                getNume().equals(eveniment.getNume()) &&
                getDescriere().equals(eveniment.getDescriere());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrganizator(), getParticipanti(), getData(), getNume(), getDescriere());
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }


    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public Eveniment(Utilizator organizator, LocalDateTime data, String nume, String descriere)
    {
        this.organizator = organizator;
        this.data = data;
        this.descriere=descriere;
        this.nume=nume;
        participanti= null;
    }


}

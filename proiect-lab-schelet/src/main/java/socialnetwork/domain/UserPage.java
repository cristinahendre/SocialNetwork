package socialnetwork.domain;

import socialnetwork.repository.page.Page;

public class UserPage {
    long id;
    String nume;
    String prenume;
    Page<Utilizator> prieteni;
    Page<CerereDePrietenie> cereri;
    Page<Message> mesajePrimite;

    public UserPage(long id, String nume, String prenume) {
        this.id = id;
        this.nume = nume;
        this.prenume = prenume;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public Page<Utilizator> getPrieteni() {
        return prieteni;
    }

    public void setPrieteni(Page<Utilizator> prieteni) {
        this.prieteni = prieteni;
    }

    public Page<CerereDePrietenie> getCereri() {
        return cereri;
    }

    public void setCereri(Page<CerereDePrietenie> cereri) {
        this.cereri = cereri;
    }

    public Page<Message> getMesajePrimite() {
        return mesajePrimite;
    }

    public void setMesajePrimite(Page<Message> mesajePrimite) {
        this.mesajePrimite = mesajePrimite;
    }
}

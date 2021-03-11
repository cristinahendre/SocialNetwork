package socialnetwork.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utilizator extends Entity<Long>{
    private String firstName;
    private String lastName;
    private List<Utilizator> friends;
    int abonat;

    /*
    emailul e de forma: 2litere nume + 2litere prenume +id @social.com
    parola e: 2020+id
    ex: 1 Aprogramioarei Ionut: email: apio1@social.com ; parola= 20201
     */
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String password;
    private String email;

    public int getAbonat() {
        return abonat;
    }

    public void setAbonat(int abonat) {
        this.abonat = abonat;
    }

    public Utilizator(String firstName, String lastName) {
        this.friends=new ArrayList<>();
        this.firstName = firstName;
        this.lastName = lastName;
        abonat=1;

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Utilizator> getFriends() {
        return friends;
    }

    /**
     *
     * @param u1 - un alt utilizator
     *          adauga un prieten nou
     */
    public void setFriends(Utilizator u1){
        friends.add(u1);

    }

    /**
     * lista de prieteni devine vida
     */
    public void setList0(){
        friends.clear();
    }

    //
    /**returneaza numarul de prieteni
    */
    public int nrFriends(){
        return friends.size();
    }

    @Override
    public String toString() {
        String friendsAsString="";
        for (Utilizator u:friends) {
            friendsAsString+=u.getLastName() +" "+u.getFirstName()+ " ;";

        }


        return "Utilizator{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", friends=" + friendsAsString +
                '}';
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator that = (Utilizator) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName());
    }
}
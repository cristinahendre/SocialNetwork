package socialnetwork.domain.validators;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;

/*
valideaza un utlizator
 */
public class RepoException extends  RuntimeException{
    public RepoException(){}

    public RepoException(String message) {
        super(message);
    }


}

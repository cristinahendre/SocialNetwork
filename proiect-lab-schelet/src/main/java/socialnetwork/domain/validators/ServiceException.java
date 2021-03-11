package socialnetwork.domain.validators;

import socialnetwork.domain.Prietenie;

public class ServiceException extends  RuntimeException{
    public ServiceException(){}

    public ServiceException(String message) {
        super(message);
    }


}

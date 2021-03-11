package socialnetwork.domain.validators;

import socialnetwork.domain.Message;
import socialnetwork.domain.Prietenie;

public class MessageValidator implements Validator<Message> {
    @Override
    public void validate(Message entity) throws ValidationException {
        if(entity.getMessage().equals("")|| entity.getMessage().equals(" "))
            throw new ValidationException("Mesaj vid.");

    }
}


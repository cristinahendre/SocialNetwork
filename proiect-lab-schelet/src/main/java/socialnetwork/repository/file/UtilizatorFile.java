package socialnetwork.repository.file;

import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.domain.validators.Validator;

import java.util.List;

public class UtilizatorFile extends AbstractFileRepository<Long, Utilizator>{

    public UtilizatorFile(String fileName, Validator<Utilizator> validator) {
        super(fileName, validator);
    }

    /**
    construieste utilizatorul
     @param attributes  - o lista de stringuri
     */
    @Override
    public Utilizator extractEntity(List<String> attributes) {
        //TODO: implement method
        Utilizator user = new Utilizator(attributes.get(1),attributes.get(2));
        user.setId(Long.parseLong(attributes.get(0)));

        return user;
    }

    /**
    creaza un string ce contine datele utilizatorului
     @param entity  -un Utilizator
     */
    @Override
    protected String createEntityAsString(Utilizator entity) {
        return entity.getId()+";"+entity.getFirstName()+";"+entity.getLastName();
    }
}

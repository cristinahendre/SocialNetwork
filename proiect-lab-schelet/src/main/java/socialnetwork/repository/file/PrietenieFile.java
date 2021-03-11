package socialnetwork.repository.file;

import socialnetwork.domain.Entity;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.domain.validators.Validator;
//import sun.nio.ch.Util;

import java.time.LocalDateTime;
import java.util.List;

public class PrietenieFile extends AbstractFileRepository<Tuple<Long,Long>,Prietenie> {
    public PrietenieFile(String fileName, Validator<Prietenie> validator) {
        super(fileName, validator);
    }



    /**
    extrage datele din fisier si construieste prietenia
     @param attributes  - o lista de stringuri
     */
    @Override
    public Prietenie extractEntity(List<String> attributes) {

        Prietenie pr=new Prietenie();
        pr.setId(new Tuple<>(Long.parseLong(attributes.get(0)),
                Long.parseLong(attributes.get(1))));
        pr.setDate(LocalDateTime.parse(attributes.get(2)));
        return pr;
    }


    /**
    creeaza entitatea pentru a fi scrisa in fisier
     @param entity  - o Prietenie
     */
    @Override
    protected String createEntityAsString(Prietenie entity) {
        return entity.getId().getLeft()
                +";"+ entity.getId().getRight() +";"+entity.getDate();
    }


}
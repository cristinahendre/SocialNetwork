package socialnetwork.domain.validators;

import socialnetwork.domain.Prietenie;


public class PrietenieValidator  implements Validator<Prietenie>{
    /**
    Valideaza o prietenie
     @param entity  - o entitate =Prietenie
 */
    @Override
    public void validate(Prietenie entity) throws ValidationException {
        if(entity.getId().getLeft() < 1 ||
           entity.getId().getRight() < 1)
            throw new ValidationException("Id negativ.");
        if(entity.getId().getRight() == entity.getId().getLeft()){
            throw  new ValidationException("Acelasi id!!");
        }

    }
}

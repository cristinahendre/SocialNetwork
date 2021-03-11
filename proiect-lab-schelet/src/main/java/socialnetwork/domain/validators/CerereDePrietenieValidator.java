package socialnetwork.domain.validators;

import socialnetwork.domain.CerereDePrietenie;


public class CerereDePrietenieValidator  implements Validator<CerereDePrietenie>{

    /**
     Valideaza o cerere de prietenie
     @param entity  - o entitate = Cerere de Prietenie
     */

    @Override
    public void validate(CerereDePrietenie entity) throws ValidationException {

        if(!entity.getStatus().equals("approved") && !entity.getStatus().equals("pending") &&
                !entity.getStatus().equals("rejected")){
            throw  new ValidationException("Status necorespunzator.");
        }

    }
}

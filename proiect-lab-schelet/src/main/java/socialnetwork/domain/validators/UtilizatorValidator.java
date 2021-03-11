package socialnetwork.domain.validators;

import socialnetwork.domain.Utilizator;

public class UtilizatorValidator implements Validator<Utilizator> {
    /**
     *
     * @param entity - o entitate =Utilizator
     * @throws ValidationException
     */
    @Override
    public void validate(Utilizator entity) throws ValidationException {
        if(entity.getId() < 0)
            throw new ValidationException("Id-ul e negativ!");
        if(entity.getId() >='a' && entity.getId() <='z')
            throw  new ValidationException("Id-ul e litera!!");
        if(entity.getId() >='A' && entity.getId() <='Z')
            throw  new ValidationException("Id-ul e litera!!");
        if(entity.getFirstName() ==" " || entity.getFirstName()=="")
            throw  new ValidationException("Prenumele e vid!!");
        if(entity.getLastName() ==" " || entity.getLastName()=="")
            throw  new ValidationException("Numele de familie e vid!!");
        if(entity.getFirstName().equals(".") || entity.getFirstName().equals("?")||
           entity.getFirstName().equals("&") || entity.getFirstName().equals(";") ||
           entity.getFirstName().startsWith("%") || entity.getFirstName().startsWith("@"))
            throw  new ValidationException("Prenumele e un simbol!!");
        if(entity.getFirstName().startsWith("0") || entity.getFirstName().startsWith("1")
        || entity.getFirstName().startsWith("2") || entity.getFirstName().startsWith("3")||
            entity.getFirstName().startsWith("4") || entity.getFirstName().startsWith("5")||
            entity.getFirstName().startsWith("6") || entity.getFirstName().startsWith("7") ||
            entity.getFirstName().startsWith("8") || entity.getFirstName().startsWith("9"))
            throw  new ValidationException("Prenumele e cifra!!");

        if(entity.getLastName().equals(".") || entity.getLastName().equals("?")||
                entity.getLastName().equals("&") || entity.getLastName().equals(";") ||
                entity.getLastName().startsWith("%") || entity.getLastName().startsWith("@"))
            throw  new ValidationException("Numele de familie e un simbol!!");
        if(entity.getLastName().startsWith("0") || entity.getLastName().startsWith("1")
                || entity.getLastName().startsWith("2") || entity.getLastName().startsWith("3")||
                entity.getLastName().startsWith("4") || entity.getLastName().startsWith("5")||
                entity.getLastName().startsWith("6") || entity.getLastName().startsWith("7") ||
                entity.getLastName().startsWith("8") || entity.getLastName().startsWith("9"))
            throw  new ValidationException("Numele de familie e cifra!!");


    }
}

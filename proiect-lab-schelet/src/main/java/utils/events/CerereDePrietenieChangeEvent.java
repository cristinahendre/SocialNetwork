package utils.events;


import socialnetwork.domain.CerereDePrietenie;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;

public class CerereDePrietenieChangeEvent implements Event {
    private ChangeEventType type;
    private CerereDePrietenie data, oldData;

    public CerereDePrietenieChangeEvent(ChangeEventType type, CerereDePrietenie data) {
        this.type = type;
        this.data = data;
    }
    public CerereDePrietenieChangeEvent(ChangeEventType type, CerereDePrietenie data, CerereDePrietenie oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public CerereDePrietenie getData() {
        return data;
    }

    public CerereDePrietenie getOldData() {
        return oldData;
    }
}
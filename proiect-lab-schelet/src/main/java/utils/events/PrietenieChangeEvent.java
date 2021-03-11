package utils.events;


import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;

public class PrietenieChangeEvent implements Event {
    private ChangeEventType type;
    private Prietenie data, oldData;

    public PrietenieChangeEvent(ChangeEventType type, Prietenie data) {
        this.type = type;
        this.data = data;
    }
    public PrietenieChangeEvent(ChangeEventType type, Prietenie data, Prietenie oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Prietenie getData() {
        return data;
    }

    public Prietenie getOldData() {
        return oldData;
    }
}
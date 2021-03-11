package utils.events;


import socialnetwork.domain.Eveniment;
import socialnetwork.domain.Message;

public class EvenimentEvent implements Event {
    private ChangeEventType type;
    private Eveniment data, oldData;

    public EvenimentEvent(ChangeEventType type, Eveniment data) {
        this.type = type;
        this.data = data;
    }
    public EvenimentEvent(ChangeEventType type, Eveniment data,
                          Eveniment oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Eveniment getData() {
        return data;
    }

    public Eveniment getOldData() {
        return oldData;
    }
}
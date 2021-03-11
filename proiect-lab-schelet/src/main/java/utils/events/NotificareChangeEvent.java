package utils.events;


import socialnetwork.domain.CerereDePrietenie;
import socialnetwork.domain.Notificare;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;

public class NotificareChangeEvent implements Event {
    private ChangeEventType type;
    private Notificare data, oldData;

    public NotificareChangeEvent(ChangeEventType type, Notificare data) {
        this.type = type;
        this.data = data;
    }
    public NotificareChangeEvent(ChangeEventType type, Notificare data, Notificare oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Notificare getData() {
        return data;
    }

    public Notificare getOldData() {
        return oldData;
    }
}
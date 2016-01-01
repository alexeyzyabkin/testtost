package com.invizorys.mobile.data;

import android.content.Context;

import com.invizorys.mobile.model.Event;
import com.letionik.matinee.EventDto;

import java.util.List;

import io.realm.Realm;

/**
 * Created by Paryshkura Roman on 26.12.2015.
 */
public class EventDataSource {
    public static final String ID = "id";
    private String LOG_TAG = "PersonRealmDataSource";
    private Realm realm;

    public EventDataSource(Context context) {
        realm = Realm.getInstance(context);
    }

    public void saveEvent(EventDto eventDto) {
//        Event event = realm.createObject(Event.class);
        Event event = new Event(eventDto);
        realm.copyToRealmOrUpdate(event);
    }

    public List<Event> getAllEvents() {
        return realm.where(Event.class).findAll();
    }

    public Event findEventById(int eventId) {
        return realm.where(Event.class).equalTo(ID, eventId).findFirst();
    }

    public void clearTable() {
        realm.clear(Event.class);
    }

    public void beginTransaction() {
        realm.beginTransaction();
    }

    public void endTransaction() {
        realm.commitTransaction();
    }
}

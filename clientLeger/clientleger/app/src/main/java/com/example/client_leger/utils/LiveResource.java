package com.example.client_leger.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class LiveResource<T> extends MutableLiveData<Resource<T>> {

    public LiveResource() {
        super();
    }

    public LiveResource(@NonNull Resource.DataStatus status, T data) {
        super(new Resource<>(status, data));
    }

    public T getData() {
        return getValue().getData();
    }

    public Resource.DataStatus getStatus() {
        return getValue().getStatus();
    }

    public void postStatus(@NonNull Resource.DataStatus status) {
        getValue().setStatus(status);
        notifyObservers();
    }

    public void postResource(@NonNull Resource.DataStatus status, T data) {
        postValue(new Resource<>(status, data));
    }

    public void notifyObservers() {
        postValue(getValue());
    }
}

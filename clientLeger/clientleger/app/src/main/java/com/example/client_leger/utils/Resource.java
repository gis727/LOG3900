package com.example.client_leger.utils;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Resource<T> {
    @NonNull
    private DataStatus status;

    @Nullable
    private T data;

    public Resource() {
        this.status = DataStatus.NONE;
        this.data = null;
    }

    public Resource(@NonNull DataStatus status, T data) {
        this.status = status;
        this.data = data;
    }

    public DataStatus getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public void setStatus(@NonNull DataStatus status) {
        this.status = status;
    }

    public void setData(@Nullable T data) {
        this.data = data;
    }

    public enum DataStatus {
        SERVER_ERROR(-2),
        BAD_FORMAT(-1),
        SUCCESS(0),
        ID_ALREADY_USED(1),
        ALREADY_CONNECTED(2),
        WRONG_CREDENTIALS(3),
        ROOM_DOESNT_EXIST(4),
        NONE(5),
        ROOM_CREATED(6),
        ROOM_HISTORY_FETCHED(7),
        MESSAGE_RECEIVED(8);

        private final int value;
        private static final Map<Integer, DataStatus> map = new HashMap<Integer, DataStatus>();

        DataStatus(final int newValue) {
            value = newValue;
        }

        public int getValue() {
            return value;
        }

        static {
            for (DataStatus type : DataStatus.values()) {
                map.put(type.value, type);
            }
        }

        public static DataStatus valueOf(int i) {
            return map.get(i);
        }
    }
}

package com.sparta.demo.util;

import javax.persistence.EntityNotFoundException;

public class RoomNotFoundException extends EntityNotFoundException {

    public RoomNotFoundException(String roomId) {
        super(roomId + " room is not found ");
    }
}

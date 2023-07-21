package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.storage.RequestStorage;

@Service
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestStorage requestStorage;

    @Autowired
    public RequestServiceImpl(RequestStorage requestStorage) {
        this.requestStorage = requestStorage;
    }
}

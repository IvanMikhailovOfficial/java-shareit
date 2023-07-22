package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.service.RequestService;

@RequestMapping("/requests")
@RestController
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;
}

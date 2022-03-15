package ch.uzh.ifi.sopra22.soprafs22server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This is main controller, if we want to get some static information about the API, we can use it such as methods to get
 * basic API structure and so on
 */
@Controller
public class ApplicationController {

    @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String helloWorld() {
        return "The application is running.";
    }
}

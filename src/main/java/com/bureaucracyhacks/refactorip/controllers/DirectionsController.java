package com.bureaucracyhacks.refactorip.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class contains the endpoints for the directions API
 */
@RequestMapping("/api/directions")
@RestController
public class DirectionsController
{

    /**
     * This method returns the directions for a given route
     * @return the directions for a given route
     */
    @GetMapping("/get-directions")
    public String getDirections()
    {
        //make a call to DirectionsService
        return null;
    }

}

package com.bureaucracyhacks.refactorip.controllers;

import com.bureaucracyhacks.refactorip.MapsUtility.Direction;
import com.bureaucracyhacks.refactorip.services.DirectionService;
import com.google.gson.Gson;
import com.google.maps.model.GeocodingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains the endpoints for the directions API
 */
@RequestMapping("/api/directions")
@RestController
public class DirectionsController
{

    /**
     * This method returns the directions for a given route
     *
     * @return the directions for a given route
     */
    @Autowired
    private DirectionService directionService;

//    @Autowired
//    private Root root;

    @GetMapping("/get-directions")
    public ResponseEntity<String> getDirections(@RequestBody String routes) throws IOException
    {
        //System.out.println(routes);
        //separator for the waypoints is a comma
        String[] waypoints = routes.split(";");
        //TODO make a call to DirectionsService
        List<GeocodingResult[]> coords  = directionService.getDirections(waypoints);
        //serialize the response into an array of class 'Direction'
        List<Direction> directions = new ArrayList<>();
        for (GeocodingResult[] coord : coords)
        {
            directions.add(new Direction(coord[0].geometry.location.lat + "", coord[0].geometry.location.lng + ""));
        }
        Gson gson = new Gson();
        //convert the response to JSON
        String json = gson.toJson(directions);

        //System.out.println(directions);
        //return the response as a JSON
        return ResponseEntity.ok(json);

        //root = directionService.getDirections(waypoints);

//        for (String Waypoint : waypoints)
//        {
//            System.out.println(Waypoint);
//        }
//        StringBuilder directions = null;
//        for (Route route : root.routes)
//        {
//            for (Leg leg : route.legs)
//            {
//                directions = new StringBuilder();
//                for (Step step : leg.steps)
//                {
//                    try
//                    {
//                        // System.out.println(step.html_instructions);
//                        //directions.append(step.polyline.toString());
//                        //System.out.println(step.polyline.toString());
//                    } catch (NullPointerException e)
//                    {
//                        System.out.println("No directions found!");
//                    }
//                }
//
//            }
//        }
//        //just for testing purposes
       // return ResponseEntity.ok(coords.toString());

    }
}

package com.bureaucracyhacks.refactorip.services;

import com.bureaucracyhacks.refactorip.MapsUtility.Leg;
import com.bureaucracyhacks.refactorip.MapsUtility.Root;
import com.bureaucracyhacks.refactorip.MapsUtility.Route;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This class models the service for the directions API that uses Googles
 * directions API
 */
public class DirectionService
{
    //TODO define autowired dependencies for UserTasks , Tasks etc

    public DirectionService()
    {
        //TODO define autowired dependencies for UserTasks , Tasks etc
    }

    public boolean getDirections() throws IOException
    {

        //get the api from the .env file
        Dotenv dotenv = Dotenv.configure().filename("configure.env").ignoreIfMalformed().ignoreIfMissing().load();

        String api_key = dotenv.get("API_KEY_MAPS");



        //TODO make a call to DirectionsService
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(api_key)
                .build();
        //list to store the waypoints

        List<GeocodingResult[]> waypoints = new ArrayList<>();
        List<String> test_waypoints = new ArrayList<>();

        test_waypoints.add("Marasesti str independentei nr 12");
        test_waypoints.add("Tecuci str Gheorghe Petrascu nr 21");
        test_waypoints.add("Marasesti str Republicii nr 1");


        for (String waypoint : test_waypoints)
        {
            try
            {
                GeocodingResult[] results = GeocodingApi.geocode(context,
                        waypoint).await();
                waypoints.add(results);
            } catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
        }
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?language=ro&origin=");
        url.append(waypoints.get(0)[0].geometry.location); //add the origin as the first waypoint
        url.append("&destination=").append(waypoints.get(waypoints.size() - 1)[0].geometry.location); //add the destination as the last waypoint
        url.append("&mode=walking&key=").append(api_key);

        //add the waypoints in between if there are any
        if (waypoints.size() > 2)
        {

            url.append("&waypoints=optimize:true"); //optimize the waypoints

            for (int i = 1; i < waypoints.size() - 1; i++)
            {
                url.append(waypoints.get(i)[0].geometry.location);
                if (i != waypoints.size() - 2)
                {
                    url.append("|");
                }
            }
        }

        System.out.println(url);
        //make the request
        URL request_url = new URL(url.toString());
        HttpURLConnection connection = (HttpURLConnection) request_url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int code = connection.getResponseCode();
        if (code != 200)
        {
            System.out.println("The response code is " + code);
            return false;
        }
        System.out.println("The response code is " + code);

        //get the response
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));

        String inputLine;

        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
        {
            content.append(inputLine);
        }
        //System.out.println(content);
        in.close();

        //deserializing the response
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Root root = gson.fromJson(String.valueOf(content), Root.class);
        for (Route ruta : root.routes)
        {
            for (Leg leg : ruta.legs)
            {
                leg.steps.forEach(step ->
                {
                    System.out.println(step.html_instructions);
                    System.out.println(step.duration.text);
                    System.out.println(step.distance.text);
                });
            }
        }


        context.shutdown();

        return true;
    }

}

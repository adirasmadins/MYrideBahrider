package rider.dev.asliborneo.app.myridebah.Model;

import java.util.List;

public class Directions {
    public List<GeocodedWaypoint> geocoded_waypoints;
    public List<Route> routes;
    public String status;
}
class GeocodedWaypoint
{
    public String geocoder_status;
    public String place_id;
    public List<String> types;
}

class Northeast
{
    public double lat;
    public double lng;
}

class Southwest
{
    public double lat;
    public double lng;
}

class Bounds
{
    public Northeast northeast;
    public Southwest southwest;
}

class EndLocation
{
    public double lat;
    public double lng;
}

class StartLocation
{
    public double lat;
    public double lng;
}

class Polyline
{
    public String points;
}

class Step
{
    public Distance distance;
    public Duration duration;
    public EndLocation end_location;
    public String html_instructions;
    public Polyline polyline;
    public  StartLocation start_location;
    public String travel_mode;
    public String maneuver;
}

class OverviewPolyline
{
    public String points;
}


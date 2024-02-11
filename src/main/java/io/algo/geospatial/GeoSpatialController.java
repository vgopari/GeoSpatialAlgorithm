package io.algo.geospatial;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class GeoSpatialController {

    private final GeoSpatialClusteringService geoSpatialClusteringService;

    public GeoSpatialController(GeoSpatialClusteringService geoSpatialClusteringService) {
        this.geoSpatialClusteringService = geoSpatialClusteringService;
    }

    @GetMapping("/map")
    public String showMap(Model model, @RequestParam Double lat,
                          @RequestParam Double lng,
                          @RequestParam Integer noOfPartitions,
                          @RequestParam Integer noOfLocations) {
        Map<Integer, List<LatLngDto>> data = geoSpatialClusteringService.partition(lat, lng, noOfPartitions, noOfLocations).getBody();
        model.addAttribute("data", data);
        model.addAttribute("centerLat", lat);
        model.addAttribute("centerLng", lng);
        return "map";
    }

}

package io.algo.geospatial;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.lang.Math.*;

@Service
public class GeoSpatialClusteringService {

    private final Random random = new Random();
    public ResponseEntity<Map<Integer, List<LatLngDto>>> partition(Double lat, Double lng, Integer size, Integer noOfLocations) {

        try {
            // Central location
            LatLngDto centralLocation = new LatLngDto(lat, lng); // New York City, NY

            // Generate 40 LatLngDto objects in all cardinal directions
            Set<LatLngDto> locations = generateLocations(centralLocation, noOfLocations);

            // Find the center of all coordinates
            LatLngDto center = findCenter(locations);

            // Create slices based on directions
            Map<Integer, List<LatLngDto>> slices = createDirectionSlices(locations, center, size);

            return ResponseEntity.ok(slices);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Collections.emptyMap());
        }
    }

    private Set<LatLngDto> generateLocations(LatLngDto centralLocation, int count) {
        Set<LatLngDto> locations = new HashSet<>();
        Random random = new Random();

        // Define the parameters for density variation
        double sigmaLat = 0.01; // Standard deviation for latitude (adjust for density)
        double sigmaLng = 0.01; // Standard deviation for longitude (adjust for density)

        // Calculate the number of points to generate in each direction
        int countOne = count * 80 / 100;  // 80% of total count for one direction
        int countTwo = count * 10 / 100;  // 10% of total count for another direction
        int countThree = count * 5 / 100; // 5% of total count for third direction
        int countFour = count - countOne - countTwo - countThree; // Remaining for fourth direction

        // Generate points for the first direction (80%)
        for (int i = 0; i < countOne; i++) {
            double latOffset = nextGaussian(random) * sigmaLat;
            double lngOffset = nextGaussian(random) * sigmaLng;
            double newLat = centralLocation.getLat() + latOffset;
            double newLng = centralLocation.getLng() + lngOffset;
            locations.add(new LatLngDto(newLat, newLng));
        }

        // Generate points for the second direction (10%)
        for (int i = 0; i < countTwo; i++) {
            double latOffset = nextGaussian(random) * sigmaLat;
            double lngOffset = nextGaussian(random) * sigmaLng;
            double newLat = centralLocation.getLat() - latOffset; // Opposite direction
            double newLng = centralLocation.getLng() - lngOffset; // Opposite direction
            locations.add(new LatLngDto(newLat, newLng));
        }

        // Generate points for the third direction (5%)
        for (int i = 0; i < countThree; i++) {
            double latOffset = nextGaussian(random) * sigmaLat;
            double lngOffset = nextGaussian(random) * sigmaLng;
            double newLat = centralLocation.getLat() + latOffset;
            double newLng = centralLocation.getLng() - lngOffset; // Opposite direction
            locations.add(new LatLngDto(newLat, newLng));
        }

        // Generate points for the fourth direction (5%)
        for (int i = 0; i < countFour; i++) {
            double latOffset = nextGaussian(random) * sigmaLat;
            double lngOffset = nextGaussian(random) * sigmaLng;
            double newLat = centralLocation.getLat() - latOffset; // Opposite direction
            double newLng = centralLocation.getLng() + lngOffset;
            locations.add(new LatLngDto(newLat, newLng));
        }

        return locations;
    }

    // Helper function to generate random values following a Gaussian distribution (Box-Muller transform)
    private double nextGaussian(Random random) {
        double u, v, s;
        do {
            u = 2.0 * random.nextDouble() - 1.0;
            v = 2.0 * random.nextDouble() - 1.0;
            s = u * u + v * v;
        } while (s >= 1.0 || s == 0.0);
        return u * Math.sqrt(-2.0 * Math.log(s) / s);
    }



    public static Map<Integer, List<LatLngDto>> createDirectionSlices(Set<LatLngDto> latLngs, LatLngDto center, Integer partitions) {
        Map<Integer, List<LatLngDto>> slices = new HashMap<>();
        List<HeadingAccountPair> headingAccountPairs = new ArrayList<>();
        // Calculate headings
        for (LatLngDto latLngDto : latLngs) {
            double heading = computeHeading(center, latLngDto);
            latLngDto.setHeading(heading);
            headingAccountPairs.add(new HeadingAccountPair(heading, latLngDto));
        }

        // Sort heading-account pairs
        headingAccountPairs.sort(Comparator.comparing(HeadingAccountPair::getHeading));

        // Distribute accounts among slices while maintaining the sorted order
        int accountsPerSlice = headingAccountPairs.size() / partitions;
        int remainingAccounts = headingAccountPairs.size() % partitions;
        int startIndex = 0;
        for (int i = 0; i < partitions; i++) {
            int endIndex = startIndex + accountsPerSlice + (i < remainingAccounts ? 1 : 0);
            List<LatLngDto> sliceAccounts = headingAccountPairs.subList(startIndex, endIndex).stream()
                    .map(HeadingAccountPair::getLatLngDto)
                    .toList();
            slices.put(i+1, sliceAccounts);
            startIndex = endIndex;
        }
        return slices;
    }

    private static LatLngDto findCenter(Set<LatLngDto> coordinates) {
        double sumLat = 0;
        double sumLng = 0;
        int errorCount = 0;
        for (LatLngDto coordinate : coordinates) {
            sumLat += coordinate.getLat();
            sumLng += coordinate.getLng();
        }
        double avgLat = sumLat / (coordinates.size() - errorCount);
        double avgLng = sumLng / (coordinates.size() - errorCount);

        return new LatLngDto(avgLat, avgLng);
    }

    public static double computeHeading(LatLngDto from, LatLngDto to) {
        double fromLat = toRadians(from.getLat());
        double fromLng = toRadians(from.getLng());
        double toLat = toRadians(to.getLat());
        double toLng = toRadians(to.getLng());
        double dLng = toLng - fromLng;
        double heading = atan2(
                sin(dLng) * cos(toLat),
                cos(fromLat) * sin(toLat) - sin(fromLat) * cos(toLat) * cos(dLng));
        return wrap(toDegrees(heading), -180, 180);
    }

    static double wrap(double n, double min, double max) {
        return (n >= min && n < max) ? n : (mod(n - min, max - min) + min);
    }

    static double mod(double x, double m) {
        return ((x % m) + m) % m;
    }
}

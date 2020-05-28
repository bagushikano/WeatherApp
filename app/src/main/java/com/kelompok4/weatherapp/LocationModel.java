package com.kelompok4.weatherapp;

import java.io.Serializable;

public class LocationModel implements Serializable {
    public static class Location {
        private String country;
        private String city;
        private String locationId;

        public Location(String City, String Country, String LocationId) {
            city = City;
            country = Country;
            locationId = LocationId;
        }

        public String getCityName() {
            return city;
        }

        public void setCityName(String City) {
            city = City;
        }

        public String getCountryName() {
            return country;
        }

        public void setCountryName(String Country) {
            country = Country;
        }

        public String getLocationId() {
            return locationId;
        }

        public void setLocationId(String LocationId) {
            locationId = LocationId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Location)) return false;

            Location location = (Location) o;

            if (!locationId.equals(location.locationId)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return locationId.hashCode();
        }
    }
}

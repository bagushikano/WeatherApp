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
    }
}

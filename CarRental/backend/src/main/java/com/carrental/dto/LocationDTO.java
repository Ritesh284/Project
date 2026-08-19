package com.carrental.dto;

import com.carrental.entity.City;
import com.carrental.entity.Location;
import com.carrental.entity.State;

public class LocationDTO {

    public static class StateResponse {
        private String id;
        private String name;

        public StateResponse() {
        }

        public StateResponse(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public StateResponse(State state) {
            if (state != null) {
                this.id = state.getId();
                this.name = state.getName();
            }
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class CityResponse {
        private String id;
        private String name;
        private String stateId;

        public CityResponse() {
        }

        public CityResponse(String id, String name, String stateId) {
            this.id = id;
            this.name = name;
            this.stateId = stateId;
        }

        public CityResponse(City city) {
            if (city != null) {
                this.id = city.getId();
                this.name = city.getName();
                this.stateId = city.getStateId();
            }
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStateId() {
            return stateId;
        }

        public void setStateId(String stateId) {
            this.stateId = stateId;
        }
    }

    public static class LocationResponse {
        private String id;
        private String name;
        private String address;
        private String cityId;

        public LocationResponse() {
        }

        public LocationResponse(String id, String name, String address, String cityId) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.cityId = cityId;
        }

        public LocationResponse(Location location) {
            if (location != null) {
                this.id = location.getId();
                this.name = location.getName();
                this.address = location.getAddress();
                this.cityId = location.getCityId();
            }
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCityId() {
            return cityId;
        }

        public void setCityId(String cityId) {
            this.cityId = cityId;
        }
    }
}

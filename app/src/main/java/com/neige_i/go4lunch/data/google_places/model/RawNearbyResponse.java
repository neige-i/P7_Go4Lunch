package com.neige_i.go4lunch.data.google_places.model;


import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class RawNearbyResponse {

    @Nullable
    @SerializedName("html_attributions")
    @Expose
    private List<Object> htmlAttributions = null;
    @Nullable
    @SerializedName("next_page_token")
    @Expose
    private String nextPageToken;
    @Nullable
    @SerializedName("results")
    @Expose
    private List<Result> results = null;
    @Nullable
    @SerializedName("status")
    @Expose
    private String status;

    @Nullable
    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(@Nullable List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    @Nullable
    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(@Nullable String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    @Nullable
    public List<Result> getResults() {
        return results;
    }

    public void setResults(@Nullable List<Result> results) {
        this.results = results;
    }

    @Nullable
    public String getStatus() {
        return status;
    }

    public void setStatus(@Nullable String status) {
        this.status = status;
    }

    public static class Result {

        @Nullable
        @SerializedName("business_status")
        @Expose
        private String businessStatus;
        @Nullable
        @SerializedName("geometry")
        @Expose
        private Geometry geometry;
        @Nullable
        @SerializedName("icon")
        @Expose
        private String icon;
        @Nullable
        @SerializedName("name")
        @Expose
        private String name;
        @Nullable
        @SerializedName("opening_hours")
        @Expose
        private OpeningHours openingHours;
        @Nullable
        @SerializedName("photos")
        @Expose
        private List<Photo> photos = null;
        @Nullable
        @SerializedName("place_id")
        @Expose
        private String placeId;
        @Nullable
        @SerializedName("plus_code")
        @Expose
        private PlusCode plusCode;
        @Nullable
        @SerializedName("rating")
        @Expose
        private Double rating;
        @Nullable
        @SerializedName("reference")
        @Expose
        private String reference;
        @Nullable
        @SerializedName("scope")
        @Expose
        private String scope;
        @Nullable
        @SerializedName("types")
        @Expose
        private List<String> types = null;
        @Nullable
        @SerializedName("user_ratings_total")
        @Expose
        private Integer userRatingsTotal;
        @Nullable
        @SerializedName("vicinity")
        @Expose
        private String vicinity;
        @Nullable
        @SerializedName("price_level")
        @Expose
        private Integer priceLevel;
        @Nullable
        @SerializedName("permanently_closed")
        @Expose
        private Boolean permanentlyClosed;

        @Nullable
        public String getBusinessStatus() {
            return businessStatus;
        }

        public void setBusinessStatus(@Nullable String businessStatus) {
            this.businessStatus = businessStatus;
        }

        @Nullable
        public Geometry getGeometry() {
            return geometry;
        }

        public void setGeometry(@Nullable Geometry geometry) {
            this.geometry = geometry;
        }

        @Nullable
        public String getIcon() {
            return icon;
        }

        public void setIcon(@Nullable String icon) {
            this.icon = icon;
        }

        @Nullable
        public String getName() {
            return name;
        }

        public void setName(@Nullable String name) {
            this.name = name;
        }

        @Nullable
        public OpeningHours getOpeningHours() {
            return openingHours;
        }

        public void setOpeningHours(@Nullable OpeningHours openingHours) {
            this.openingHours = openingHours;
        }

        @Nullable
        public List<Photo> getPhotos() {
            return photos;
        }

        public void setPhotos(@Nullable List<Photo> photos) {
            this.photos = photos;
        }

        @Nullable
        public String getPlaceId() {
            return placeId;
        }

        public void setPlaceId(@Nullable String placeId) {
            this.placeId = placeId;
        }

        @Nullable
        public PlusCode getPlusCode() {
            return plusCode;
        }

        public void setPlusCode(@Nullable PlusCode plusCode) {
            this.plusCode = plusCode;
        }

        @Nullable
        public Double getRating() {
            return rating;
        }

        public void setRating(@Nullable Double rating) {
            this.rating = rating;
        }

        @Nullable
        public String getReference() {
            return reference;
        }

        public void setReference(@Nullable String reference) {
            this.reference = reference;
        }

        @Nullable
        public String getScope() {
            return scope;
        }

        public void setScope(@Nullable String scope) {
            this.scope = scope;
        }

        @Nullable
        public List<String> getTypes() {
            return types;
        }

        public void setTypes(@Nullable List<String> types) {
            this.types = types;
        }

        @Nullable
        public Integer getUserRatingsTotal() {
            return userRatingsTotal;
        }

        public void setUserRatingsTotal(@Nullable Integer userRatingsTotal) {
            this.userRatingsTotal = userRatingsTotal;
        }

        @Nullable
        public String getVicinity() {
            return vicinity;
        }

        public void setVicinity(@Nullable String vicinity) {
            this.vicinity = vicinity;
        }

        @Nullable
        public Integer getPriceLevel() {
            return priceLevel;
        }

        public void setPriceLevel(@Nullable Integer priceLevel) {
            this.priceLevel = priceLevel;
        }

        @Nullable
        public Boolean getPermanentlyClosed() {
            return permanentlyClosed;
        }

        public void setPermanentlyClosed(@Nullable Boolean permanentlyClosed) {
            this.permanentlyClosed = permanentlyClosed;
        }
    }

    public static class Geometry {

        @Nullable
        @SerializedName("location")
        @Expose
        private Location location;
        @Nullable
        @SerializedName("viewport")
        @Expose
        private Viewport viewport;

        @Nullable
        public Location getLocation() {
            return location;
        }

        public void setLocation(@Nullable Location location) {
            this.location = location;
        }

        @Nullable
        public Viewport getViewport() {
            return viewport;
        }

        public void setViewport(@Nullable Viewport viewport) {
            this.viewport = viewport;
        }
    }

    public static class Location {

        @Nullable
        @SerializedName("lat")
        @Expose
        private Double lat;
        @Nullable
        @SerializedName("lng")
        @Expose
        private Double lng;

        @Nullable
        public Double getLat() {
            return lat;
        }

        public void setLat(@Nullable Double lat) {
            this.lat = lat;
        }

        @Nullable
        public Double getLng() {
            return lng;
        }

        public void setLng(@Nullable Double lng) {
            this.lng = lng;
        }
    }

    public static class Viewport {

        @Nullable
        @SerializedName("northeast")
        @Expose
        private Northeast northeast;
        @Nullable
        @SerializedName("southwest")
        @Expose
        private Southwest southwest;

        @Nullable
        public Northeast getNortheast() {
            return northeast;
        }

        public void setNortheast(@Nullable Northeast northeast) {
            this.northeast = northeast;
        }

        @Nullable
        public Southwest getSouthwest() {
            return southwest;
        }

        public void setSouthwest(@Nullable Southwest southwest) {
            this.southwest = southwest;
        }
    }

    public static class Northeast {

        @Nullable
        @SerializedName("lat")
        @Expose
        private Double lat;
        @Nullable
        @SerializedName("lng")
        @Expose
        private Double lng;

        @Nullable
        public Double getLat() {
            return lat;
        }

        public void setLat(@Nullable Double lat) {
            this.lat = lat;
        }

        @Nullable
        public Double getLng() {
            return lng;
        }

        public void setLng(@Nullable Double lng) {
            this.lng = lng;
        }
    }

    public static class Southwest {

        @Nullable
        @SerializedName("lat")
        @Expose
        private Double lat;
        @Nullable
        @SerializedName("lng")
        @Expose
        private Double lng;

        @Nullable
        public Double getLat() {
            return lat;
        }

        public void setLat(@Nullable Double lat) {
            this.lat = lat;
        }

        @Nullable
        public Double getLng() {
            return lng;
        }

        public void setLng(@Nullable Double lng) {
            this.lng = lng;
        }
    }

    public static class OpeningHours {

        @Nullable
        @SerializedName("open_now")
        @Expose
        private Boolean openNow;

        @Nullable
        public Boolean getOpenNow() {
            return openNow;
        }

        public void setOpenNow(@Nullable Boolean openNow) {
            this.openNow = openNow;
        }
    }

    public static class PlusCode {

        @Nullable
        @SerializedName("compound_code")
        @Expose
        private String compoundCode;
        @Nullable
        @SerializedName("global_code")
        @Expose
        private String globalCode;

        @Nullable
        public String getCompoundCode() {
            return compoundCode;
        }

        public void setCompoundCode(@Nullable String compoundCode) {
            this.compoundCode = compoundCode;
        }

        @Nullable
        public String getGlobalCode() {
            return globalCode;
        }

        public void setGlobalCode(@Nullable String globalCode) {
            this.globalCode = globalCode;
        }
    }

    public static class Photo {

        @Nullable
        @SerializedName("height")
        @Expose
        private Integer height;
        @Nullable
        @SerializedName("html_attributions")
        @Expose
        private List<String> htmlAttributions = null;
        @Nullable
        @SerializedName("photo_reference")
        @Expose
        private String photoReference;
        @Nullable
        @SerializedName("width")
        @Expose
        private Integer width;

        @Nullable
        public Integer getHeight() {
            return height;
        }

        public void setHeight(@Nullable Integer height) {
            this.height = height;
        }

        @Nullable
        public List<String> getHtmlAttributions() {
            return htmlAttributions;
        }

        public void setHtmlAttributions(@Nullable List<String> htmlAttributions) {
            this.htmlAttributions = htmlAttributions;
        }

        @Nullable
        public String getPhotoReference() {
            return photoReference;
        }

        public void setPhotoReference(@Nullable String photoReference) {
            this.photoReference = photoReference;
        }

        @Nullable
        public Integer getWidth() {
            return width;
        }

        public void setWidth(@Nullable Integer width) {
            this.width = width;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RawNearbyResponse that = (RawNearbyResponse) o;
        return Objects.equals(htmlAttributions, that.htmlAttributions) &&
            Objects.equals(nextPageToken, that.nextPageToken) &&
            Objects.equals(results, that.results) &&
            Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(htmlAttributions, nextPageToken, results, status);
    }
}
package com.neige_i.go4lunch.data.google_places.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

// TODO: remove overridden methods
@SuppressWarnings({"unused", "RedundantSuppression"})
public class RawDetailsResponse {

    @Nullable
    @SerializedName("html_attributions")
    @Expose
    private List<Object> htmlAttributions;
    @Nullable
    @SerializedName("result")
    @Expose
    private Result result;
    @Nullable
    @SerializedName("status")
    @Expose
    private String status;

    @Override
    public String toString() {
        return "RawDetailsResponse{" +
            "htmlAttributions=" + htmlAttributions +
            ", result=" + result +
            ", status='" + status + '\'' +
            '}';
    }

    @Nullable
    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(@Nullable List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    @Nullable
    public Result getResult() {
        return result;
    }

    public void setResult(@Nullable Result result) {
        this.result = result;
    }

    @Nullable
    public String getStatus() {
        return status;
    }

    public void setStatus(@Nullable String status) {
        this.status = status;
    }

    public static class Close {

        @Nullable
        @SerializedName("day")
        @Expose
        private Integer day;
        @Nullable
        @SerializedName("time")
        @Expose
        private String time;

        @Nullable
        public Integer getDay() {
            return day;
        }

        @Nullable
        public String getTime() {
            return time;
        }

        @NonNull
        @Override
        public String toString() {
            return "Close{" +
                "day=" + day +
                ", time='" + time + '\'' +
                '}';
        }
    }

    public static class Geometry {

        @Nullable
        @SerializedName("location")
        @Expose
        private Location location;

        @Nullable
        public Location getLocation() {
            return location;
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

        @Nullable
        public Double getLng() {
            return lng;
        }
    }

    public static class Open {

        @Nullable
        @SerializedName("day")
        @Expose
        private Integer day;
        @Nullable
        @SerializedName("time")
        @Expose
        private String time;

        @Nullable
        public Integer getDay() {
            return day;
        }

        @Nullable
        public String getTime() {
            return time;
        }

        @NonNull
        @Override
        public String toString() {
            return "Open{" +
                "day=" + day +
                ", time='" + time + '\'' +
                '}';
        }
    }

    public static class OpeningHours {

        @Nullable
        @SerializedName("open_now")
        @Expose
        private Boolean openNow;
        @Nullable
        @SerializedName("periods")
        @Expose
        private List<Period> periods;
        @Nullable
        @SerializedName("weekday_text")
        @Expose
        private List<String> weekdayText;

        @Nullable
        public Boolean getOpenNow() {
            return openNow;
        }

        @Nullable
        public List<Period> getPeriods() {
            return periods;
        }

        @Nullable
        public List<String> getWeekdayText() {
            return weekdayText;
        }

        @NonNull
        @Override
        public String toString() {
            return "OpeningHours{" +
                "openNow=" + openNow +
                ", periods=" + periods +
                ", weekdayText=" + weekdayText +
                '}';
        }
    }

    public static class Period {

        @Nullable
        @SerializedName("close")
        @Expose
        private Close close;
        @Nullable
        @SerializedName("open")
        @Expose
        private Open open;

        @Nullable
        public Close getClose() {
            return close;
        }

        @Nullable
        public Open getOpen() {
            return open;
        }

        @NonNull
        @Override
        public String toString() {
            return "Period{" +
                "close=" + close +
                ", open=" + open +
                '}';
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
        private List<String> htmlAttributions;
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

        @Nullable
        public List<String> getHtmlAttributions() {
            return htmlAttributions;
        }

        @Nullable
        public String getPhotoReference() {
            return photoReference;
        }

        @Nullable
        public Integer getWidth() {
            return width;
        }
    }

    public static class Result {
        @Override
        public String toString() {
            return "Result{" +
                "businessStatus='" + businessStatus + '\'' +
                ", formattedAddress='" + formattedAddress + '\'' +
                ", internationalPhoneNumber='" + internationalPhoneNumber + '\'' +
                ", geometry=" + geometry +
                ", name='" + name + '\'' +
                ", obfuscatedType=" + obfuscatedType +
                ", openingHours=" + openingHours +
                ", photos=" + photos +
                ", placeId='" + placeId + '\'' +
                ", rating=" + rating +
                ", website='" + website + '\'' +
                '}';
        }

        @Nullable
        @SerializedName("business_status")
        @Expose
        private String businessStatus;
        @Nullable
        @SerializedName("formatted_address")
        @Expose
        private String formattedAddress;
        @Nullable
        @SerializedName("international_phone_number")
        @Expose
        private String internationalPhoneNumber;
        @Nullable
        @SerializedName("geometry")
        @Expose
        private Geometry geometry;
        @Nullable
        @SerializedName("name")
        @Expose
        private String name;
        @Nullable
        @SerializedName("obfuscated_type")
        @Expose
        private List<Object> obfuscatedType;
        @Nullable
        @SerializedName("opening_hours")
        @Expose
        private OpeningHours openingHours;
        @Nullable
        @SerializedName("photos")
        @Expose
        private List<Photo> photos;
        @Nullable
        @SerializedName("place_id")
        @Expose
        private String placeId;
        @Nullable
        @SerializedName("rating")
        @Expose
        private Double rating;
        @Nullable
        @SerializedName("website")
        @Expose
        private String website;

        @Nullable
        public String getBusinessStatus() {
            return businessStatus;
        }

        public void setBusinessStatus(@Nullable String businessStatus) {
            this.businessStatus = businessStatus;
        }

        @Nullable
        public String getFormattedAddress() {
            return formattedAddress;
        }

        public void setFormattedAddress(@Nullable String formattedAddress) {
            this.formattedAddress = formattedAddress;
        }

        @Nullable
        public String getInternationalPhoneNumber() {
            return internationalPhoneNumber;
        }

        public void setInternationalPhoneNumber(@Nullable String internationalPhoneNumber) {
            this.internationalPhoneNumber = internationalPhoneNumber;
        }

        @Nullable
        public Geometry getGeometry() {
            return geometry;
        }

        public void setGeometry(@Nullable Geometry geometry) {
            this.geometry = geometry;
        }

        @Nullable
        public String getName() {
            return name;
        }

        public void setName(@Nullable String name) {
            this.name = name;
        }

        @Nullable
        public List<Object> getObfuscatedType() {
            return obfuscatedType;
        }

        public void setObfuscatedType(@Nullable List<Object> obfuscatedType) {
            this.obfuscatedType = obfuscatedType;
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
        public Double getRating() {
            return rating;
        }

        public void setRating(@Nullable Double rating) {
            this.rating = rating;
        }

        @Nullable
        public String getWebsite() {
            return website;
        }

        public void setWebsite(@Nullable String website) {
            this.website = website;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Result result = (Result) o;
            return Objects.equals(businessStatus, result.businessStatus) &&
                Objects.equals(formattedAddress, result.formattedAddress) &&
                Objects.equals(internationalPhoneNumber, result.internationalPhoneNumber) &&
                Objects.equals(geometry, result.geometry) &&
                Objects.equals(name, result.name) &&
                Objects.equals(obfuscatedType, result.obfuscatedType) &&
                Objects.equals(openingHours, result.openingHours) &&
                Objects.equals(photos, result.photos) &&
                Objects.equals(placeId, result.placeId) &&
                Objects.equals(rating, result.rating) &&
                Objects.equals(website, result.website);
        }

        @Override
        public int hashCode() {
            return Objects.hash(businessStatus, formattedAddress, internationalPhoneNumber, geometry, name, obfuscatedType, openingHours, photos, placeId, rating, website);
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
        RawDetailsResponse response = (RawDetailsResponse) o;
        return Objects.equals(htmlAttributions, response.htmlAttributions) &&
            Objects.equals(result, response.result) &&
            Objects.equals(status, response.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(htmlAttributions, result, status);
    }
}
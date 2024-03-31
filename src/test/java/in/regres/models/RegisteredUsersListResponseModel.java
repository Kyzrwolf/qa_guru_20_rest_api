package in.regres.models;

import java.util.List;

import com.fasterxml.jackson.annotation.*;

@lombok.Data
public class RegisteredUsersListResponseModel {
    public Integer page;
    @JsonProperty("per_page")
    public Integer perPage;
    public Integer total;
    @JsonProperty("total_pages")
    public Integer totalPages;
    public List<Data> data;
    public Support support;

    @lombok.Data
    public static class Data {
        public Integer id;
        public String email;
        @JsonProperty("first_name")
        public String firstName;
        @JsonProperty("last_name")
        public String lastName;
        public String avatar;

    }
    @lombok.Data
    public static class Support {
        public String url;
        public String text;

    }
}
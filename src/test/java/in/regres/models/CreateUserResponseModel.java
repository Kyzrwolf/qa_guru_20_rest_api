package in.regres.models;

import lombok.Data;

@Data
public class CreateUserResponseModel {
    String name;
    String job;
    String id;
    String createdAt;
}

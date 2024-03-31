package in.regres.models;

import lombok.Data;

@Data
public class UpdateUserResponseModel {
    String name;
    String job;
    String id;
    String updatedAt;
}

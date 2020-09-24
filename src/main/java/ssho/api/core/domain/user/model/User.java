package ssho.api.core.domain.user.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "user")
@Data
public class User {
    @Id
    private int id;

    private String email;
    private String password;
    private String name;
}

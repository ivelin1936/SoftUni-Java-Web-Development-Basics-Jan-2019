package org.softuni.exam.domain.models.binding.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.softuni.exam.domain.entities.User;
import org.softuni.exam.domain.models.binding.Bindable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class UserRegisterBindingModel implements Bindable<User> {

    @NotNull
    @Size(min = 1, max = 32)
    private String username;

    @NotNull
    @Size(min = 1, max = 75)
    private String password;

    @NotNull
    @Size(min = 1, max = 32)
    private String confirmPassword;

    @NotNull
    @Email
    @Size(min = 1, max = 64)
    private String email;
}

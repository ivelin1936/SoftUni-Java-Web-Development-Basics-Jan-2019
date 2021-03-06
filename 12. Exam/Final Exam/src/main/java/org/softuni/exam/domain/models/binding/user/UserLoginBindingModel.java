package org.softuni.exam.domain.models.binding.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.softuni.exam.domain.entities.User;
import org.softuni.exam.domain.models.binding.Bindable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class UserLoginBindingModel implements Bindable<User> {

    @NotNull
    @Size(min = 1, max = 32)
    private String username;

    @NotNull
    @Size(min = 1, max = 32)
    private String password;
}

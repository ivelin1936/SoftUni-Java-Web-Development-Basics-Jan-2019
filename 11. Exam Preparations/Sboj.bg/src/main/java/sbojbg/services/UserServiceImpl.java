package sbojbg.services;

import org.modelmapper.ModelMapper;
import sbojbg.domain.entities.User;
import sbojbg.domain.models.binding.user.UserLoginBindingModel;
import sbojbg.domain.models.binding.user.UserRegisterBindingModel;
import sbojbg.domain.models.view.Viewable;
import sbojbg.domain.models.view.user.UserLoggedViewModel;
import sbojbg.repositories.UserRepository;
import sbojbg.util.PasswordHasher;

import javax.inject.Inject;
import javax.validation.Validator;
import java.util.Optional;
import java.util.logging.Logger;

public class UserServiceImpl extends BaseService<User, String, UserRepository> implements UserService {

    private static final Logger LOG = Logger.getLogger(UserServiceImpl.class.getName());

    private final PasswordHasher passwordHasher;

    @Inject
    public UserServiceImpl(UserRepository repository,
                           ModelMapper mapper,
                           Validator validator,
                           PasswordHasher passwordHasher) {
        super(mapper, validator, repository);
        this.passwordHasher = passwordHasher;
    }

    @Override
    protected Logger logger() {
        return LOG;
    }

    @Override
    public boolean register(UserRegisterBindingModel model) {
        if (model == null || !model.getPassword().equals(model.getConfirmPassword())) {
            return false;
        }

        String encodedPassword = passwordHasher.encodedHash(model.getPassword().toCharArray());
        model.setPassword(encodedPassword);

        return create(model);
    }

    @Override
    public Optional<UserLoggedViewModel> login(UserLoginBindingModel model) {
        if (model == null || !validator.validate(model).isEmpty()) {
            return Optional.empty();
        }
        return repository
                .findByUsername(model.getUsername())
                .filter(u -> passwordHasher.verifyEncoded(u.getPassword(), model.getPassword().toCharArray()))
                .map(u -> mapper.map(u, UserLoggedViewModel.class));
    }

    @Override
    public <M extends Viewable<User>> Optional<M> findByUsername(String username, Class<M> clazz) {
        return repository
                .findByUsername(username)
                .map(e -> mapper.map(e, clazz));
    }
}

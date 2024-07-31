package dev.omerdanismaz.WSIW.services;

import dev.omerdanismaz.WSIW.enums.EUserStatus;
import dev.omerdanismaz.WSIW.models.UserModel;
import dev.omerdanismaz.WSIW.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements IUserService
{
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void createUser(UserModel userModel)
    {
        String encodedPassword = passwordEncoder.encode(userModel.getDbfUserPassword());
        userModel.setDbfUserPassword(encodedPassword);
        userModel.setDbfUserCreatedOn(new Date());
        userModel.setDbfUserLoggedOn(new Date());
        userModel.setDbfUserStatus(EUserStatus.MEMBER);
        userRepository.save(userModel);
    }

    @Override
    public UserModel readUserById(Long userId)
    {
        Optional<UserModel> searchedUser = userRepository.findById(userId);

        return searchedUser.orElseGet(() -> UserModel.builder()
                .dbfUserId(0L)
                .dbfUserFirstName("EMPTY")
                .dbfUserLastName("EMPTY")
                .dbfUserEmail("EMPTY")
                .dbfUserPassword("EMPTY")
                .dbfUserCreatedOn(new Date())
                .dbfUserLoggedOn(new Date())
                .dbfUserStatus(EUserStatus.GUEST)
                .build());
    }

    @Override
    public UserModel readUserByEmail(String userEmail)
    {
        Optional<UserModel> searchedUser = userRepository.findByDbfUserEmail(userEmail);

        return searchedUser.orElseGet(() -> UserModel.builder()
                .dbfUserId(0L)
                .dbfUserFirstName("EMPTY")
                .dbfUserLastName("EMPTY")
                .dbfUserEmail("EMPTY")
                .dbfUserPassword("EMPTY")
                .dbfUserCreatedOn(new Date())
                .dbfUserLoggedOn(new Date())
                .dbfUserStatus(EUserStatus.GUEST)
                .build());
    }

    @Override
    public void updateUser(UserModel userModel)
    {
        Optional<UserModel> searchedUser = userRepository.findById(userModel.getDbfUserId());

        if(searchedUser.isPresent())
        {
            if(searchedUser.get().getDbfUserId() != 0L)
            {
                userRepository.save(searchedUser.get());
            }
        }
    }
}

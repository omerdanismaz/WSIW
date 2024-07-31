package dev.omerdanismaz.WSIW.services;

import dev.omerdanismaz.WSIW.models.UserModel;

public interface IUserService
{
    void createUser(UserModel userModel);
    UserModel readUserById(Long userId);
    UserModel readUserByEmail(String userEmail);
    void updateUser(UserModel userModel);
}

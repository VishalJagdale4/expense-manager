package dev.vishal.auth.service;

import dev.common.exceptionutils.exceptions.BadRequestException;
import dev.vishal.auth.entity.Users;
import dev.vishal.auth.model.UserDto;

import java.util.UUID;

public interface UsersService {

    Users createUser(UserDto dto) throws BadRequestException;

    Users updateUser(UserDto dto) throws BadRequestException;

    Users updatePassword(UserDto dto) throws BadRequestException;

    Users getUser(UUID id) throws BadRequestException;

    Users getUserByEmail(String email) throws BadRequestException;

    void deleteUser(UUID id) throws BadRequestException;
}

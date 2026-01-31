package dev.vishal.expensemanager.service;

import dev.commonlib.exceptionutils.exceptions.BadRequestException;
import dev.vishal.expensemanager.dto.UserDto;
import dev.vishal.expensemanager.entity.Users;

import java.util.UUID;

public interface UsersService {

    Users createUser(UserDto dto) throws BadRequestException;

    Users updateUser(UserDto dto) throws BadRequestException;

    Users getUser(UUID id) throws BadRequestException;

    void deleteUser(UUID id) throws BadRequestException;
}

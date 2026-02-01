package dev.vishal.expensemanager.service;

import dev.common.exceptionutils.exceptions.BadRequestException;
import dev.vishal.expensemanager.dto.UserDto;
import dev.vishal.expensemanager.entity.Users;
import dev.vishal.expensemanager.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;

    @Override
    @Transactional
    public Users createUser(UserDto dto) throws BadRequestException {

        if (usersRepository.existsByEmailIgnoreCaseAndIsDeletedFalse(dto.getEmail())) {
            throw new BadRequestException("User email already exists");
        }

        Users user = new Users();
        copyDtoToEntity(dto, user);

        try {
            return usersRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            // Handles race condition safely
            throw new BadRequestException("User email already exists");
        }
    }

    @Override
    @Transactional
    public Users updateUser(UserDto dto) throws BadRequestException {

        Users user = usersRepository.findById(dto.getId())
                .filter(usr -> !usr.getIsDeleted())
                .orElseThrow(() -> new BadRequestException("User does not exists"));

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());

        return usersRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Users getUser(UUID id) throws BadRequestException {
        return usersRepository.findById(id)
                .filter(usr -> !usr.getIsDeleted())
                .orElseThrow(() -> new BadRequestException("User does not exists"));
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) throws BadRequestException {
        Users user = usersRepository.findById(id)
                .filter(usr -> !usr.getIsDeleted())
                .orElseThrow(() -> new BadRequestException("User does not exists"));
        user.setIsDeleted(true);
        usersRepository.save(user);
    }

    // ------------------ Helper methods ------------------

    private void copyDtoToEntity(UserDto dto, Users entity) {
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail().trim().toLowerCase());
    }
}

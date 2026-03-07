package dev.vishal.auth.service;

import dev.common.exceptionutils.exceptions.BadRequestException;
import dev.vishal.auth.entity.Users;
import dev.vishal.auth.model.UserDto;
import dev.vishal.auth.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

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
    @Transactional
    public Users updatePassword(UserDto dto) throws BadRequestException {

        Users user = usersRepository.findById(dto.getId())
                .filter(usr -> !usr.getIsDeleted())
                .orElseThrow(() -> new BadRequestException("User does not exists"));

        if (passwordEncoder.encode(dto.getPassword()).equals(user.getPassword())) {
            throw new BadRequestException("New password must be different from the current password");
        }

        user.setPassword(passwordEncoder.encode(dto.getPassword()));

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
    @Transactional(readOnly = true)
    public Users getUserByEmail(String emailId) throws BadRequestException {
        return usersRepository.findByEmailIgnoreCaseAndIsDeletedFalse(emailId)
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));
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
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity.setEmail(dto.getEmail().trim().toLowerCase());
    }
}

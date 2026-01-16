package ru.practicum.explorewithme.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.user.NewUserRequest;
import ru.practicum.explorewithme.dto.user.UserDto;
import ru.practicum.explorewithme.exception.ConflictException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.mapper.UserMapper;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getUsers(List<Long> ids, Pageable pageable) {
        if (ids != null && !ids.isEmpty()) {
            return userRepository.findByIdIn(ids, pageable)
                    .map(userMapper::toUserDto)
                    .getContent();
        } else {
            return userRepository.findAll(pageable)
                    .map(userMapper::toUserDto)
                    .getContent();
        }
    }

    @Transactional
    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        if (userRepository.existsByEmail(newUserRequest.getEmail())) {
            throw new ConflictException("Email must be unique");
        }

        User user = userMapper.toUser(newUserRequest);
        User savedUser = userRepository.save(user);
        return userMapper.toUserDto(savedUser);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        userRepository.deleteById(userId);
    }
}
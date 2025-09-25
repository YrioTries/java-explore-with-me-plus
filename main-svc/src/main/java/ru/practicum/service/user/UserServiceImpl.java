package ru.practicum.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exceptions.DuplicateEmailException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers(List<Integer> ids, Integer from, Integer size) {
        log.info("В сервис пришёл запрос на получение списка всех пользователей");
        PageRequest pageRequest = PageRequest.of(from / size, size);
        if (ids == null || ids.isEmpty()) {
            log.warn("Список id-шников пользователей пустой");
            log.info("Возвращаем всех имеющихся в БД пользователей");
            return userRepository.findAll(pageRequest)
                    .stream()
                    .map(userMapper::toUserDto)
                    .toList();
        }
        log.info("Возвращаем пользователей с конкретным id");
        return userRepository.findAllByIdIn(pageRequest, ids)
                .stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    @Transactional
    public UserDto addUser(NewUserRequest newUser) {
        log.info("В сервис пришёл запрос на создание пользователя");
        if (userRepository.existsByEmail(newUser.getEmail())) {
            log.error("Пользователь с email {} уже есть в БД", newUser.getEmail());
            throw new DuplicateEmailException("Пользователь с email " + newUser.getEmail() + " уже есть в БД");
        }
        User user = userRepository.save(userMapper.toUser(newUser));
        log.info("Пользователь успешно добавлен в БД. Его id={}", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("В сервис пришёл запрос на удаление пользователя");
        if (!userRepository.existsById(userId)) {
            log.error("Пользователя с id {} нет в БД. Удаление невозможно", userId);
            throw new NotFoundException("Пользователя с id " + userId + " нет в БД");
        }
        userRepository.deleteById(userId);
    }
}

package ru.practicum.user.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    public List<UserDto> getUsers(int[] ids, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);

        if (ids == null) {
            return userRepository.findAll(page).getContent().stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }
        List<Long> idsList = new ArrayList<>(ids.length);
        for (long id : ids) {
            idsList.add(id);
        }

        return userRepository.findAll(page).stream()
                .filter(user -> idsList.contains(user.getId()))
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public void deleteUser(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        userRepository.deleteById(id);
    }
}

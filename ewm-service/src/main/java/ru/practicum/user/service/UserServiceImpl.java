package ru.practicum.user.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.UpdateException;
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
        if (userRepository.existsByName(userDto.getName())) {
            throw new UpdateException("Name exist");
        }
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    public List<UserDto> getUsers(int[] ids, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        List<Long> idsToRep = new ArrayList<>();
        if (ids == null) {
            return userRepository.findAll(page).getContent().stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }
        for (int id : ids) {
            idsToRep.add((long) id);
        }
        return userRepository.findAllByIdIn(idsToRep, page).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public void deleteUser(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        userRepository.deleteById(id);
    }
}

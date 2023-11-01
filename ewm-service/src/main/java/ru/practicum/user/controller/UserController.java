package ru.practicum.user.controller;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;


import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/admin/users")
@Validated
public class UserController {
    UserService userService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    UserDto addUser(@Valid @RequestBody UserDto userDto) {
        return userService.addUser(userDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    void deleteUser(@Positive @PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping
    Collection<UserDto> getUsers(@RequestParam(required = false) int[] ids,
                                 @RequestParam(defaultValue = "10") @Positive Integer size,
                                 @RequestParam(defaultValue = "0") @PositiveOrZero Integer from) {
        return userService.getUsers(ids, from, size);
    }
}

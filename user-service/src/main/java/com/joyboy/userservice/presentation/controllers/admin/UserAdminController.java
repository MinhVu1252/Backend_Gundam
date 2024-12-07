package com.joyboy.userservice.presentation.controllers.admin;

import com.joyboy.commonservice.common.response.ResponseObject;
import com.joyboy.userservice.presentation.response.UserPageResponse;
import com.joyboy.userservice.applications.usecase.user.IUserService;
import com.joyboy.userservice.applications.utils.PageConstant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * UserAdminController handles administrative requests related to users.
 *
 * <p>This controller provides endpoints to manage users, including retrieving a list of users and blocking or enabling users.</p>
 *
 * <p><strong>Endpoints:</strong></p>
 * <ul>
 *     <li>{@code GET /admin/users}: Retrieve a paginated and sorted list of all users.</li>
 *     <li>{@code PUT /admin/users/block/{userId}/{active}}: Block or enable a user by their ID.</li>
 * </ul>
 *
 * <p><strong>Exceptions:</strong></p>
 * <ul>
 *     <li>{@link Exception}: Thrown when there are issues with blocking or enabling a user.</li>
 * </ul>
 *
 * @author ThaoDien
 * @version 1.0
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class UserAdminController {
    private final IUserService userService;

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllUserss(
            @RequestParam(defaultValue = PageConstant.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = PageConstant.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = PageConstant.SORT_BY) String permissionId,
            @RequestParam(defaultValue = PageConstant.SORT_DIR) String sortDir
    ) {
        UserPageResponse userPageResponse = userService.findAllUsers(pageNumber, pageSize, permissionId, sortDir);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(userPageResponse)
                .message("Get list of users information successfully")
                .status(HttpStatus.OK)
                .build());
    }

    @PutMapping("/block/{userId}/{active}")
    public ResponseEntity<ResponseObject> blockOrEnable(
            @Valid @PathVariable long userId,
            @Valid @PathVariable int active
    ) throws Exception {
        userService.blockOrEnable(userId, active > 0);
        String message = active > 0 ? "Successfully enabled the user." : "Successfully blocked the user.";
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(message)
                .status(HttpStatus.OK)
                .data(null)
                .build());
    }
}

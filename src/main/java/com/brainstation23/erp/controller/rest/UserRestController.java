package com.brainstation23.erp.controller.rest;


import com.brainstation23.erp.mapper.UserMapper;
import com.brainstation23.erp.model.dto.CreateUserRequest;
import com.brainstation23.erp.model.dto.UserResponse;
import com.brainstation23.erp.model.dto.UpdateUserRequest;
import com.brainstation23.erp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.UUID;

@Tag(name = "User")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "api/users")
public class UserRestController {
	private final UserService userService;
	private final UserMapper userMapper;

	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
	@Operation(summary = "Get All Users")
	@GetMapping
	public ResponseEntity<Page<UserResponse>> getAll(@ParameterObject Pageable pageable) {
		log.info("Getting List of Users");
		var users = userService.getAll(pageable);
		return ResponseEntity.ok(users.map(userMapper::domainToResponse));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get Single User")
	@GetMapping("{id}")
	public ResponseEntity<UserResponse> getOne(@PathVariable UUID id) {
		log.info("Getting Details of User({})", id);
		var user = userService.getOne(id);
		return ResponseEntity.ok(userMapper.domainToResponse(user));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Create Single User")
	@PostMapping
	public ResponseEntity<Void> createOne(@RequestBody @Valid CreateUserRequest createRequest) {
		log.info("Creating an User: {} ", createRequest);
		var id = userService.createOne(createRequest);
		var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
		return ResponseEntity.created(location).build();
	}

	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Update Single User")
	@PutMapping("{id}")
	public ResponseEntity<Void> updateOne(@PathVariable UUID id,
			@RequestBody @Valid UpdateUserRequest updateRequest) {
		log.info("Updating an User({}): {} ", id, updateRequest);
		userService.updateOne(id, updateRequest);
		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Delete Single User")
	@DeleteMapping("{id}")
	public ResponseEntity<Void> deleteOne(@PathVariable UUID id) {
		log.info("Deleting an User({}) ", id);
		userService.deleteOne(id);
		return ResponseEntity.noContent().build();
	}
}

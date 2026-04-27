package com.climbup.service;

import com.climbup.dto.MemberRequest;
import com.climbup.model.User;
import com.climbup.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final UserRepository userRepo;

    public Page<User> listMembers(String search, Pageable pageable) {
        return userRepo.findMembers(search, pageable);
    }

    public User getById(Long id) {
        return userRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));
    }

    public User create(MemberRequest req) {
        if (userRepo.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        User u = new User();
        u.setName(req.name());
        u.setEmail(req.email());
        u.setPhone(req.phone());
        u.setRole(User.Role.MEMBER);
        return userRepo.save(u);
    }

    public User update(Long id, MemberRequest req) {
        User u = getById(id);
        u.setName(req.name());
        u.setEmail(req.email());
        u.setPhone(req.phone());
        return userRepo.save(u);
    }

    public void delete(Long id) {
        userRepo.delete(getById(id));
    }

    public long countMembers() {
        return userRepo.findMembers(null, Pageable.unpaged()).getTotalElements();
    }
}

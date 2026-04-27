package com.climbup.service;

import com.climbup.dto.MembershipTypeRequest;
import com.climbup.model.MembershipType;
import com.climbup.repository.MembershipTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipTypeService {

    private final MembershipTypeRepository repo;

    public List<MembershipType> listAll() {
        return repo.findAll();
    }

    public List<MembershipType> listActive() {
        return repo.findByIsActiveTrue();
    }

    public MembershipType getById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership type not found"));
    }

    public MembershipType create(MembershipTypeRequest req) {
        MembershipType t = new MembershipType();
        apply(t, req);
        return repo.save(t);
    }

    public MembershipType update(Long id, MembershipTypeRequest req) {
        MembershipType t = getById(id);
        apply(t, req);
        return repo.save(t);
    }

    public void deactivate(Long id) {
        MembershipType t = getById(id);
        t.setActive(false);
        repo.save(t);
    }

    private void apply(MembershipType t, MembershipTypeRequest req) {
        t.setName(req.name());
        t.setDescription(req.description());
        t.setBasePrice(req.basePrice());
        t.setDurationDays(req.durationDays());
        t.setEntriesCount(req.entriesCount());
        t.setEntriesValidityDays(req.entriesValidityDays());
        t.setDiscountPct(req.discountPct());
    }
}

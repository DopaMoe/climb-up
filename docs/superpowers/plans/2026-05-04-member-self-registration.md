# Member Self-Registration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Allow prospective gym members to register themselves online, pick a plan, and have their membership activated by an admin after paying in person.

**Architecture:** A new public `POST /api/public/register` endpoint creates a `User` + `Membership(status=PENDING, startDate=null)`. A new admin `GET /api/admin/memberships/pending` endpoint lists pending memberships (searchable by member ID). The existing activate endpoint is extended to accept a `startDate`, which triggers date computation in `MembershipService`. Frontend adds a `/register` page and an admin `/admin/pending-activations` page.

**Tech Stack:** Spring Boot 3 / JPA / H2 (backend), Vue 3 / TypeScript / Pinia / Tailwind (frontend)

---

## File Map

| Action | File |
|---|---|
| Modify | `backend/src/main/java/com/climbup/model/Membership.java` |
| Modify | `backend/src/main/java/com/climbup/service/MembershipService.java` |
| Modify | `backend/src/main/java/com/climbup/repository/MembershipRepository.java` |
| Modify | `backend/src/main/java/com/climbup/controller/admin/AdminMemberController.java` |
| Create | `backend/src/main/java/com/climbup/dto/RegisterRequest.java` |
| Create | `backend/src/main/java/com/climbup/dto/ActivateMembershipRequest.java` |
| Create | `backend/src/main/java/com/climbup/controller/PublicController.java` |
| Create | `backend/src/test/java/com/climbup/PublicControllerTest.java` |
| Create | `backend/src/test/java/com/climbup/MembershipActivationTest.java` |
| Modify | `frontend/src/api/admin.ts` |
| Create | `frontend/src/api/public.ts` |
| Create | `frontend/src/views/RegisterView.vue` |
| Create | `frontend/src/views/admin/PendingActivationsView.vue` |
| Modify | `frontend/src/router/index.ts` |
| Modify | `frontend/src/components/layout/AdminLayout.vue` |
| Modify | `frontend/src/views/LoginView.vue` |

---

## Task 1: Relax `Membership.startDate` nullability

**Files:**
- Modify: `backend/src/main/java/com/climbup/model/Membership.java:29-30`

- [ ] **Step 1: Change startDate column to nullable**

In `Membership.java`, change line 29 from:
```java
@Column(nullable = false)
private LocalDate startDate;
```
to:
```java
@Column(nullable = true)
private LocalDate startDate;
```

- [ ] **Step 2: Verify the app still starts**

```bash
cd backend && ./mvnw spring-boot:run -q &
sleep 8 && curl -s http://localhost:8080/api/admin/dashboard | grep -q "totalMembers" && echo "OK" || echo "FAIL"
kill %1
```
Expected: `OK`

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/climbup/model/Membership.java
git commit -m "feat: allow null startDate on Membership for pending self-registrations"
```

---

## Task 2: Add DTOs

**Files:**
- Create: `backend/src/main/java/com/climbup/dto/RegisterRequest.java`
- Create: `backend/src/main/java/com/climbup/dto/ActivateMembershipRequest.java`

- [ ] **Step 1: Create `RegisterRequest.java`**

```java
package com.climbup.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
    @NotBlank String name,
    @NotBlank @Email String email,
    String phone,
    @NotNull Long membershipTypeId
) {}
```

- [ ] **Step 2: Create `ActivateMembershipRequest.java`**

```java
package com.climbup.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ActivateMembershipRequest(
    @NotNull LocalDate startDate
) {}
```

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/climbup/dto/RegisterRequest.java \
        backend/src/main/java/com/climbup/dto/ActivateMembershipRequest.java
git commit -m "feat: add RegisterRequest and ActivateMembershipRequest DTOs"
```

---

## Task 3: Update `MembershipService.activate()` to accept a start date

**Files:**
- Modify: `backend/src/main/java/com/climbup/service/MembershipService.java:54-63`

- [ ] **Step 1: Write a failing test**

Create `backend/src/test/java/com/climbup/MembershipActivationTest.java`:

```java
package com.climbup;

import com.climbup.model.Membership;
import com.climbup.model.MembershipType;
import com.climbup.model.User;
import com.climbup.repository.MembershipRepository;
import com.climbup.repository.MembershipTypeRepository;
import com.climbup.repository.UserRepository;
import com.climbup.service.MembershipService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MembershipActivationTest {

    @Autowired MembershipService membershipService;
    @Autowired MembershipRepository membershipRepo;
    @Autowired MembershipTypeRepository typeRepo;
    @Autowired UserRepository userRepo;

    @Test
    void activate_setsStartDateAndComputesEndDate() {
        User user = new User();
        user.setName("Test"); user.setEmail("t@t.com");
        userRepo.save(user);

        MembershipType type = new MembershipType();
        type.setName("Monthly"); type.setBasePrice(BigDecimal.valueOf(50));
        type.setDurationDays(30);
        typeRepo.save(type);

        Membership m = new Membership();
        m.setUser(user); m.setMembershipType(type);
        m.setPricePaid(BigDecimal.valueOf(50));
        m.setStatus(Membership.Status.PENDING);
        membershipRepo.save(m);

        LocalDate start = LocalDate.of(2026, 6, 1);
        Membership activated = membershipService.activate(m.getId(), start);

        assertThat(activated.getStatus()).isEqualTo(Membership.Status.ACTIVE);
        assertThat(activated.getStartDate()).isEqualTo(start);
        assertThat(activated.getEndDate()).isEqualTo(LocalDate.of(2026, 7, 1));
    }

    @Test
    void activate_entryBased_setsExpiryDate() {
        User user = new User();
        user.setName("Test2"); user.setEmail("t2@t.com");
        userRepo.save(user);

        MembershipType type = new MembershipType();
        type.setName("10-Pack"); type.setBasePrice(BigDecimal.valueOf(80));
        type.setEntriesCount(10); type.setEntriesValidityDays(60);
        typeRepo.save(type);

        Membership m = new Membership();
        m.setUser(user); m.setMembershipType(type);
        m.setPricePaid(BigDecimal.valueOf(80));
        m.setStatus(Membership.Status.PENDING);
        membershipRepo.save(m);

        LocalDate start = LocalDate.of(2026, 6, 1);
        Membership activated = membershipService.activate(m.getId(), start);

        assertThat(activated.getStatus()).isEqualTo(Membership.Status.ACTIVE);
        assertThat(activated.getStartDate()).isEqualTo(start);
        assertThat(activated.getEntriesExpiryDate()).isEqualTo(LocalDate.of(2026, 7, 31));
    }
}
```

- [ ] **Step 2: Run to confirm FAIL**

```bash
cd backend && ./mvnw test -pl . -Dtest=MembershipActivationTest -q 2>&1 | tail -10
```
Expected: compilation error — `activate(Long, LocalDate)` not defined yet.

- [ ] **Step 3: Update `MembershipService.activate()`**

Replace the entire `activate` method (lines 54–63) in `MembershipService.java`:

```java
public Membership activate(Long membershipId, LocalDate startDate) {
    Membership m = getById(membershipId);
    if (m.getStatus() != Membership.Status.PENDING) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Membership is not pending");
    }
    m.setStartDate(startDate);
    applyDates(m, m.getMembershipType(), startDate);
    m.setStatus(Membership.Status.ACTIVE);
    Membership saved = repo.save(m);
    eventPublisher.publishEvent(new MembershipActivatedEvent(saved));
    return saved;
}
```

- [ ] **Step 4: Fix the existing call site in `AdminMemberController.activate()`**

In `AdminMemberController.java`, the `activate` endpoint (line 67–70) currently calls `membershipService.activate(id)`. Update it to accept a request body and pass `startDate`. The full updated method:

```java
@PutMapping("/memberships/{id}/activate")
public Membership activate(@PathVariable Long id,
                           @Valid @RequestBody ActivateMembershipRequest req) {
    return membershipService.activate(id, req.startDate());
}
```

Add the import at the top of `AdminMemberController.java`:
```java
import com.climbup.dto.ActivateMembershipRequest;
```

- [ ] **Step 5: Run tests — expect PASS**

```bash
cd backend && ./mvnw test -pl . -Dtest=MembershipActivationTest -q 2>&1 | tail -5
```
Expected: `BUILD SUCCESS`

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/climbup/service/MembershipService.java \
        backend/src/main/java/com/climbup/controller/admin/AdminMemberController.java \
        backend/src/test/java/com/climbup/MembershipActivationTest.java
git commit -m "feat: activate membership with admin-chosen start date"
```

---

## Task 4: Add pending memberships query to `MembershipRepository`

**Files:**
- Modify: `backend/src/main/java/com/climbup/repository/MembershipRepository.java`

- [ ] **Step 1: Add two query methods**

Append to `MembershipRepository.java` (after line 20, before the closing `}`):

```java
List<Membership> findByStatus(Membership.Status status);

List<Membership> findByStatusAndUserId(Membership.Status status, Long userId);
```

Add the import at the top (it's a JPA derived query — no `@Query` annotation needed):
The file already imports `List` — no new imports needed.

- [ ] **Step 2: Verify compilation**

```bash
cd backend && ./mvnw compile -q 2>&1 | tail -5
```
Expected: `BUILD SUCCESS`

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/climbup/repository/MembershipRepository.java
git commit -m "feat: add findByStatus queries to MembershipRepository"
```

---

## Task 5: Create `PublicController` with self-registration endpoint

**Files:**
- Create: `backend/src/main/java/com/climbup/controller/PublicController.java`
- Create: `backend/src/test/java/com/climbup/PublicControllerTest.java`

- [ ] **Step 1: Write the failing test**

Create `backend/src/test/java/com/climbup/PublicControllerTest.java`:

```java
package com.climbup;

import com.climbup.repository.MembershipRepository;
import com.climbup.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PublicControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired UserRepository userRepo;
    @Autowired MembershipRepository membershipRepo;

    @BeforeEach
    void clean() {
        membershipRepo.deleteAll();
        userRepo.deleteAll();
    }

    @Test
    void register_createsUserAndPendingMembership() throws Exception {
        // Membership type with id=1 is seeded by DataSeeder
        var body = Map.of(
            "name", "Alice Gym",
            "email", "alice@gym.com",
            "phone", "0501234567",
            "membershipTypeId", 1
        );

        mvc.perform(post("/api/public/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(body)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").isNumber())
            .andExpect(jsonPath("$.status").value("PENDING"));

        assertThat(userRepo.existsByEmail("alice@gym.com")).isTrue();
        assertThat(membershipRepo.findAll()).hasSize(1);
        assertThat(membershipRepo.findAll().get(0).getStartDate()).isNull();
    }

    @Test
    void register_duplicateEmail_returns409() throws Exception {
        var body = Map.of(
            "name", "Alice Gym",
            "email", "alice@gym.com",
            "membershipTypeId", 1
        );
        mvc.perform(post("/api/public/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(body)))
            .andExpect(status().isCreated());

        mvc.perform(post("/api/public/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(body)))
            .andExpect(status().isConflict());
    }
}
```

- [ ] **Step 2: Run to confirm FAIL**

```bash
cd backend && ./mvnw test -pl . -Dtest=PublicControllerTest -q 2>&1 | tail -10
```
Expected: `404 NOT_FOUND` — endpoint not yet defined.

- [ ] **Step 3: Create `PublicController.java`**

```java
package com.climbup.controller;

import com.climbup.dto.RegisterRequest;
import com.climbup.model.Membership;
import com.climbup.model.MembershipType;
import com.climbup.model.User;
import com.climbup.repository.MembershipRepository;
import com.climbup.repository.UserRepository;
import com.climbup.service.DiscountService;
import com.climbup.service.MembershipTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final UserRepository userRepo;
    private final MembershipRepository membershipRepo;
    private final MembershipTypeService typeService;
    private final DiscountService discountService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepo.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        User user = new User();
        user.setName(req.name());
        user.setEmail(req.email());
        user.setPhone(req.phone());
        user.setRole(User.Role.MEMBER);
        userRepo.save(user);

        MembershipType type = typeService.getById(req.membershipTypeId());

        Membership membership = new Membership();
        membership.setUser(user);
        membership.setMembershipType(type);
        membership.setPricePaid(discountService.computeEffectivePrice(type));
        membership.setStatus(Membership.Status.PENDING);
        // startDate intentionally null — set by admin on activation
        membershipRepo.save(membership);

        return Map.of(
            "userId", user.getId(),
            "name", user.getName(),
            "membershipId", membership.getId(),
            "membershipTypeName", type.getName(),
            "status", membership.getStatus().name()
        );
    }
}
```

- [ ] **Step 4: Run tests — expect PASS**

```bash
cd backend && ./mvnw test -pl . -Dtest=PublicControllerTest -q 2>&1 | tail -5
```
Expected: `BUILD SUCCESS`

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/climbup/controller/PublicController.java \
        backend/src/test/java/com/climbup/PublicControllerTest.java
git commit -m "feat: add POST /api/public/register for member self-registration"
```

---

## Task 6: Add pending activations endpoint to `AdminMemberController`

**Files:**
- Modify: `backend/src/main/java/com/climbup/controller/admin/AdminMemberController.java`

- [ ] **Step 1: Add the endpoint**

Add the following method to `AdminMemberController.java`, after the `cancel` method (after line 75):

```java
@GetMapping("/memberships/pending")
public List<Map<String, Object>> pendingMemberships(
    @RequestParam(required = false) Long memberId
) {
    List<Membership> memberships = memberId != null
        ? membershipRepo.findByStatusAndUserId(Membership.Status.PENDING, memberId)
        : membershipRepo.findByStatus(Membership.Status.PENDING);

    return memberships.stream().map(m -> Map.<String, Object>of(
        "membershipId", m.getId(),
        "userId", m.getUser().getId(),
        "memberName", m.getUser().getName(),
        "membershipTypeName", m.getMembershipType().getName(),
        "createdAt", m.getCreatedAt()
    )).toList();
}
```

Add these fields/imports at the top of the class. The class already has `MembershipService`; add a direct `MembershipRepository` injection for the new query methods:

```java
private final MembershipRepository membershipRepo;
```

Add the import:
```java
import com.climbup.repository.MembershipRepository;
```

- [ ] **Step 2: Verify compilation and all tests pass**

```bash
cd backend && ./mvnw test -q 2>&1 | tail -5
```
Expected: `BUILD SUCCESS`

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/climbup/controller/admin/AdminMemberController.java
git commit -m "feat: add GET /api/admin/memberships/pending with optional memberId filter"
```

---

## Task 7: Update frontend admin API

**Files:**
- Modify: `frontend/src/api/admin.ts`

- [ ] **Step 1: Update `activateMembership` and add `getPendingMemberships`**

Replace the existing `activateMembership` function (line 29–31):
```ts
export const activateMembership = (id: number) =>
  api.put(`/api/admin/memberships/${id}/activate`).then(r => r.data)
```
with:
```ts
export const activateMembership = (id: number, startDate: string) =>
  api.put(`/api/admin/memberships/${id}/activate`, { startDate }).then(r => r.data)

export const getPendingMemberships = (memberId?: number) =>
  api.get('/api/admin/memberships/pending', { params: memberId ? { memberId } : {} }).then(r => r.data)
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/api/admin.ts
git commit -m "feat: update activateMembership to send startDate, add getPendingMemberships"
```

---

## Task 8: Create `public.ts` API module

**Files:**
- Create: `frontend/src/api/public.ts`

- [ ] **Step 1: Create the file**

```ts
import api from './axios'

export interface RegisterRequest {
  name: string
  email: string
  phone?: string
  membershipTypeId: number
}

export const registerMember = (data: RegisterRequest) =>
  api.post('/api/public/register', data).then(r => r.data)
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/api/public.ts
git commit -m "feat: add public.ts API module for self-registration"
```

---

## Task 9: Create `RegisterView.vue`

**Files:**
- Create: `frontend/src/views/RegisterView.vue`

- [ ] **Step 1: Create the file**

```vue
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getMembershipTypes } from '../api/member'
import { registerMember } from '../api/public'

const router = useRouter()
const auth = useAuthStore()

const form = ref({ name: '', email: '', phone: '', membershipTypeId: 0 })
const plans = ref<any[]>([])
const loading = ref(false)
const error = ref('')
const result = ref<{ userId: number; name: string; membershipTypeName: string } | null>(null)

onMounted(async () => {
  plans.value = await getMembershipTypes()
  if (plans.value.length) form.value.membershipTypeId = plans.value[0].id
})

async function submit() {
  error.value = ''
  loading.value = true
  try {
    result.value = await registerMember({
      name: form.value.name,
      email: form.value.email,
      phone: form.value.phone || undefined,
      membershipTypeId: form.value.membershipTypeId,
    })
  } catch (e: any) {
    error.value = e.response?.data?.message ?? 'Registration failed. Please try again.'
  } finally {
    loading.value = false
  }
}

function goToDashboard() {
  if (!result.value) return
  auth.enterAs('MEMBER', result.value.userId)
  router.push('/member/dashboard')
}
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-gray-900 to-blue-900 flex items-center justify-center p-4">
    <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md p-8">

      <!-- Success state -->
      <div v-if="result" class="text-center space-y-4">
        <div class="text-5xl">🎉</div>
        <h2 class="text-2xl font-bold text-gray-900">Registration Submitted!</h2>
        <p class="text-gray-600">Welcome, <strong>{{ result.name }}</strong>. Your <em>{{ result.membershipTypeName }}</em> membership is pending activation.</p>
        <div class="bg-blue-50 border border-blue-200 rounded-xl p-4 space-y-1">
          <p class="text-sm text-blue-700 font-medium">Your Member ID</p>
          <p class="text-4xl font-bold text-blue-600">#{{ result.userId }}</p>
          <p class="text-xs text-blue-500">Save this ID — you'll need it to log in.</p>
        </div>
        <p class="text-sm text-gray-500">Visit the gym to pay and your membership will be activated.</p>
        <button @click="goToDashboard"
          class="w-full bg-blue-600 text-white py-3 rounded-xl font-semibold hover:bg-blue-700 transition-colors">
          Go to My Dashboard
        </button>
      </div>

      <!-- Registration form -->
      <div v-else>
        <div class="text-center mb-6">
          <div class="text-5xl mb-3">🧗</div>
          <h1 class="text-2xl font-bold text-gray-900">Join ClimbUp</h1>
          <p class="text-gray-500 mt-1 text-sm">Register and pick your membership</p>
        </div>

        <form @submit.prevent="submit" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Full Name</label>
            <input v-model="form.name" required type="text" placeholder="Your name"
              class="w-full border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Email</label>
            <input v-model="form.email" required type="email" placeholder="you@example.com"
              class="w-full border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Phone <span class="text-gray-400">(optional)</span></label>
            <input v-model="form.phone" type="tel" placeholder="05XXXXXXXX"
              class="w-full border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Membership Plan</label>
            <select v-model="form.membershipTypeId" required
              class="w-full border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
              <option v-for="plan in plans" :key="plan.id" :value="plan.id">
                {{ plan.name }} — {{ plan.effectivePrice }} SAR
              </option>
            </select>
            <p class="text-xs text-gray-400 mt-1">Start date will be set when you pay at the gym.</p>
          </div>

          <p v-if="error" class="text-red-500 text-sm">{{ error }}</p>

          <button type="submit" :disabled="loading"
            class="w-full bg-blue-600 text-white py-3 rounded-xl font-semibold hover:bg-blue-700 disabled:opacity-50 transition-colors">
            {{ loading ? 'Submitting...' : 'Register' }}
          </button>
        </form>

        <p class="text-center text-sm text-gray-500 mt-4">
          Already have an account?
          <RouterLink to="/login" class="text-blue-600 hover:underline">Log in</RouterLink>
        </p>
      </div>

    </div>
  </div>
</template>
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/views/RegisterView.vue
git commit -m "feat: add member self-registration page"
```

---

## Task 10: Create `PendingActivationsView.vue`

**Files:**
- Create: `frontend/src/views/admin/PendingActivationsView.vue`

- [ ] **Step 1: Create the file**

```vue
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getPendingMemberships, activateMembership } from '../../api/admin'

const rows = ref<any[]>([])
const searchId = ref('')
const modal = ref<{ membershipId: number; memberName: string; planName: string } | null>(null)
const startDate = ref('')
const activating = ref(false)
const activateError = ref('')

async function load(memberId?: number) {
  rows.value = await getPendingMemberships(memberId)
}

onMounted(() => load())

function search() {
  const id = Number(searchId.value)
  load(id || undefined)
}

function openModal(row: any) {
  modal.value = { membershipId: row.membershipId, memberName: row.memberName, planName: row.membershipTypeName }
  startDate.value = ''
  activateError.value = ''
}

async function confirmActivation() {
  if (!modal.value || !startDate.value) return
  activating.value = true
  activateError.value = ''
  try {
    await activateMembership(modal.value.membershipId, startDate.value)
    rows.value = rows.value.filter(r => r.membershipId !== modal.value!.membershipId)
    modal.value = null
  } catch (e: any) {
    activateError.value = e.response?.data?.message ?? 'Activation failed.'
  } finally {
    activating.value = false
  }
}
</script>

<template>
  <div class="p-8">
    <h1 class="text-2xl font-bold text-gray-900 mb-6">Pending Activations</h1>

    <!-- Search -->
    <div class="flex gap-3 mb-6">
      <input v-model="searchId" type="number" placeholder="Search by Member ID"
        class="border border-gray-300 rounded-lg px-4 py-2 text-sm w-56 focus:outline-none focus:ring-2 focus:ring-blue-500"
        @keyup.enter="search" />
      <button @click="search"
        class="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm hover:bg-blue-700 transition-colors">
        Search
      </button>
      <button v-if="searchId" @click="searchId = ''; load()"
        class="text-sm text-gray-500 hover:text-gray-700 underline">
        Clear
      </button>
    </div>

    <!-- Table -->
    <div v-if="rows.length" class="bg-white rounded-xl shadow overflow-hidden">
      <table class="w-full text-sm">
        <thead class="bg-gray-50 border-b">
          <tr>
            <th class="text-left px-6 py-3 font-semibold text-gray-600">Member ID</th>
            <th class="text-left px-6 py-3 font-semibold text-gray-600">Name</th>
            <th class="text-left px-6 py-3 font-semibold text-gray-600">Plan</th>
            <th class="text-left px-6 py-3 font-semibold text-gray-600">Requested</th>
            <th class="px-6 py-3"></th>
          </tr>
        </thead>
        <tbody class="divide-y">
          <tr v-for="row in rows" :key="row.membershipId" class="hover:bg-gray-50">
            <td class="px-6 py-4 font-mono text-blue-600">#{{ row.userId }}</td>
            <td class="px-6 py-4 font-medium text-gray-900">{{ row.memberName }}</td>
            <td class="px-6 py-4 text-gray-600">{{ row.membershipTypeName }}</td>
            <td class="px-6 py-4 text-gray-400 text-xs">{{ new Date(row.createdAt).toLocaleDateString() }}</td>
            <td class="px-6 py-4 text-right">
              <button @click="openModal(row)"
                class="bg-green-600 text-white px-3 py-1.5 rounded-lg text-xs font-medium hover:bg-green-700 transition-colors">
                Activate
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-else class="text-center py-16 text-gray-400">
      <p class="text-4xl mb-3">✅</p>
      <p class="font-medium">No pending activations</p>
    </div>

    <!-- Activation modal -->
    <div v-if="modal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-white rounded-2xl shadow-2xl p-8 w-full max-w-sm">
        <h2 class="text-lg font-bold text-gray-900 mb-1">Activate Membership</h2>
        <p class="text-sm text-gray-500 mb-5">
          {{ modal.memberName }} — <em>{{ modal.planName }}</em>
        </p>
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-1">Start Date</label>
          <input v-model="startDate" type="date" required
            class="w-full border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500" />
        </div>
        <p v-if="activateError" class="text-red-500 text-sm mb-3">{{ activateError }}</p>
        <div class="flex gap-3">
          <button @click="modal = null"
            class="flex-1 border border-gray-300 text-gray-700 py-2 rounded-lg text-sm hover:bg-gray-50 transition-colors">
            Cancel
          </button>
          <button @click="confirmActivation" :disabled="!startDate || activating"
            class="flex-1 bg-green-600 text-white py-2 rounded-lg text-sm font-medium hover:bg-green-700 disabled:opacity-50 transition-colors">
            {{ activating ? 'Activating...' : 'Confirm' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/views/admin/PendingActivationsView.vue
git commit -m "feat: add PendingActivationsView with search and activation modal"
```

---

## Task 11: Wire up router, sidebar, and login link

**Files:**
- Modify: `frontend/src/router/index.ts`
- Modify: `frontend/src/components/layout/AdminLayout.vue`
- Modify: `frontend/src/views/LoginView.vue`

- [ ] **Step 1: Add routes to `router/index.ts`**

Add the `/register` public route after the `/login` route (after line 13):
```ts
{ path: '/register', component: () => import('../views/RegisterView.vue') },
```

Add the pending-activations child route inside the `/admin` children array (after the `discounts` entry, before the closing `]`):
```ts
{ path: 'pending-activations', component: () => import('../views/admin/PendingActivationsView.vue') },
```

- [ ] **Step 2: Add nav link to `AdminLayout.vue`**

Add the following `RouterLink` to the `<nav>` in `AdminLayout.vue`, after the Discounts link (after line 47):

```html
<RouterLink to="/admin/pending-activations"
  class="flex items-center gap-3 px-3 py-2 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors"
  active-class="bg-blue-600 text-white">
  <span>⏳</span> Pending Activations
</RouterLink>
```

- [ ] **Step 3: Add "Register" link to `LoginView.vue`**

Add a register link at the bottom of the card, after the `</div>` that closes the `space-y-4` div (after line 75). Insert before the closing `</div>` of the card:

```html
<p class="text-center text-sm text-gray-500 mt-4">
  New member?
  <RouterLink to="/register" class="text-blue-600 hover:underline font-medium">Register here</RouterLink>
</p>
```

- [ ] **Step 4: Commit**

```bash
git add frontend/src/router/index.ts \
        frontend/src/components/layout/AdminLayout.vue \
        frontend/src/views/LoginView.vue
git commit -m "feat: wire up /register and /admin/pending-activations routes, nav, and login link"
```

---

## Task 12: End-to-end verification

- [ ] **Step 1: Start the backend**

```bash
cd backend && ./mvnw spring-boot:run &
```
Wait for `Started ClimbUpApplication`.

- [ ] **Step 2: Start the frontend**

```bash
cd frontend && npm run dev &
```
Wait for `Local: http://localhost:5173/`.

- [ ] **Step 3: Register a new member**

1. Open http://localhost:5173/register
2. Fill in: Name=`Sarah Climber`, Email=`sarah@test.com`, Phone=`0501111111`, pick any plan
3. Submit → confirmation panel appears with a Member ID (e.g. `#5`)
4. Click "Go to My Dashboard" → redirected to `/member/dashboard`
5. Dashboard shows the membership with **PENDING** status badge

- [ ] **Step 4: Verify member can log back in**

1. Click logout
2. At `/login`, enter the member ID shown in step 3
3. Click "Continue as Member" → dashboard loads with the PENDING membership

- [ ] **Step 5: Admin activates the membership**

1. Reload, enter as Admin
2. Click "Pending Activations" in sidebar
3. See Sarah's row; search by her member ID to confirm filter works
4. Click "Activate" → modal opens
5. Pick a start date → click Confirm → row disappears

- [ ] **Step 6: Verify activation from member side**

1. Logout → log in as member with Sarah's ID
2. Dashboard now shows **ACTIVE** status with the correct start and end dates

- [ ] **Step 7: Run all backend tests**

```bash
cd backend && ./mvnw test -q 2>&1 | tail -5
```
Expected: `BUILD SUCCESS`

# Project Development Phases and Time Estimates

---

## Phase 1: Project Setup and Environment Configuration (18h)
| Task | Estimated Time |
|------|----------------|
| Project structure, Maven dependencies, package architecture | 4h             |
| PostgreSQL database + Hibernate + JPA configuration | 4h             |
| JWT security configuration (filters, provider, authentication layer) | 6h             |
| Redis cache configuration & integration | 2h             |

---

## Phase 2: Core Entities & JPA Repositories (12h)
| Task | Estimated Time |
|------|----------------|
| User entity & repository | 4h |
| Comment entity & repository | 4h |
| GameObject entity & repository | 4h |

---

## Phase 3: Authentication and User Flow (22h)
| Task                                                   | Estimated Time |
|--------------------------------------------------------|----------------|
| User registration & email confirmation                 | 6h |
| Login & JWT token generation                           | 4h |
| Token validation inside request filter                 | 4h |
| Role-based authorization (ANONYMMOUS / SELLER / ADMIN) | 4h |
| Password hashing & validation                          | 4h |

---

## Phase 4: Seller Management & Ranking Logic (19h)
| Task | Estimated Time |
|------|----------------|
| Seller profile structure & response DTO | 4h             |
| Seller approval logic | 5h             |
| Seller listing with pagination & filtering | 4h             |
| Top Sellers ranking by average rating & sorting | 6h             |

---

## Phase 5: Comment & Rating System (18h)
| Task | Estimated Time |
|------|----------------|
| Comment posting & validation | 4h |
| Anonymous comment handling (email field) | 2h |
| Comment approval workflow | 4h |
| Rating calculation (AVG & COUNT) | 4h |
| Mapping entity → DTO & formatting | 4h |

---

## Phase 6: Game Object Management (10h)
| Task | Estimated Time |
|------|----------------|
| Seller creates GameObject (title + text) | 4h |
| GameObject updating & deletion | 4h |
| Response DTO formatting | 2h |

---

## Phase 7: Testing (19h)
| Task | Estimated Time |
|------|----------------|
| Unit test setup + Mockito | 4h             |
| Unit tests for RatingService & SellerService | 4h             |
| Integration test environment w/ Testcontainers | 5h             |
| Integration tests for SellerController & GameObjectController | 6h             |

---

## Phase 8: Documentation & Final Polish (12h)
| Task                                  | Estimated Time |
|---------------------------------------|----------------|
| documentation       | 4h             |
| Cleanup, code refactoring, formatting | 8h             |

---

# ✅ Total Estimated Development Time: **130 hours**
---


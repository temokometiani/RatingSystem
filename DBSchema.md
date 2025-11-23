# 📦 Database Schema Documentation

This document describes the structure of the database, including entities, attributes, keys, and relationships.

---

# 🧍 USERS table

The `USERS` table stores all user accounts in the system, including authentication data and status fields.

### Attributes:
- **id** — integer primary key, auto-incremented
- **first_name** — optional text field for first name
- **last_name** — optional text field for last name
- **email** — unique value used for login
- **password** — hashed password
- **role** — one of: `ADMIN`, `SELLER`, `ANONYMMOUS`
- **created_at** — timestamp indicating when the account was created
- **approved** — boolean indicating whether the account is activated
- **email_confirmed** — boolean indicating whether email is verified

### Relationships:
- A user can **write multiple comments** (via `author_id`)
- A user can **receive multiple comments as a seller** (via `seller_id`)
- A user can **create multiple game objects** (via `user_id`)

---

# 🎮 GAME_OBJECTS table

The `GAME_OBJECTS` table stores game-related entries created by users.

### Attributes:
- **id** — integer primary key, auto-incremented
- **title** — non-nullable string
- **text** — text body content
- **user_id** — foreign key referencing `users.id`, identifies the creator
- **created_at** — timestamp marking creation
- **updated_at** — timestamp marking last update

### Relationships:
- Each game object belongs to **exactly one user**
- A user can own **many game objects**

---

# 💬 COMMENTS table

The `COMMENTS` table stores feedback messages and ratings from users.

### Attributes:
- **id** — integer primary key, auto-incremented
- **message** — comment text
- **author_id** — foreign key referencing the user who wrote the comment
- **anonymous_email** — optional email used if comment was submitted anonymously
- **seller_id** — foreign key referencing the user being reviewed
- **created_at** — timestamp of when the comment was created
- **approved** — boolean indicating whether the comment was moderator-approved
- **rating** — numeric rating between 1 and 5

### Relationships:
- One user (author) can write **many comments**
- One user (seller) can receive **many comments**
- Each comment has exactly **one author** and exactly **one recipient (seller)**

---




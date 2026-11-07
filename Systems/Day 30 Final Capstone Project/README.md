# Day 30 – Final Capstone: IronCore Fitness Gym Management System

A menu-driven gym management system in pure Java (no external libraries, **Java 17+**).
It brings together everything from Days 1–29: input validation, loops, collections, OOP,
inheritance and polymorphism, file I/O, dates, authentication and reports in one layered application.

Data is saved as CSV files in a `data/` folder after every action and reloaded on the next run.
The first run creates realistic sample data, dated relative to today, so every report has something to show.

## How to run

**IntelliJ / NetBeans / VS Code:** open this folder, mark `src` as the source root, run `gym.Main`.

**Command line (Windows):**
```
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
java -cp out gym.Main
```

**Command line (macOS / Linux / Git Bash):**
```
javac -d out $(find src -name "*.java")
java -cp out gym.Main
```

### Default accounts (first run)
| Username | Temporary password | Role |
|---|---|---|
| `admin` | `admin1234` | Administrator: everything, including reports and user accounts |
| `staff` | `staff1234` | Front Desk Staff: members, payments, check-ins, bookings |

You must choose a new password at first login. Delete the `data/` folder to reset to the sample data.
Tip: whenever the system asks for a member, you can type the ID (`M-0001`) **or** part of the name (`juan`).

## Features

| Module | What it does |
|---|---|
| Login | Salted PBKDF2 password hashing, 3 attempts per login, forced password change for temporary passwords |
| Members | Register (Regular / Student / Senior), list with filter + sort, search, profile, update, delete (admin) |
| Memberships & Payments | Plan price list, new/renewal payment with discount, cash/GCash/card, change, printed receipt, history, reprint |
| Check-in & Attendance | One check-in per member per day for active members only, daily list, member visit history |
| Classes & Trainers | Weekly schedule, roster, book/cancel, create/reschedule/resize/delete classes, trainer CRUD and schedules |
| Reports (admin) | Dashboard, revenue by month/plan/method, expiring & expired list, 14-day attendance bar chart, top visitors, class utilization, export to `.txt` |
| User accounts (admin) | List, create, reset password, delete, view activity log |

### Business rules enforced
- Mobile numbers must be 11 digits starting with 09 (`0917 123 4567` and `+63…` are normalized) and are unique per member.
- Student and Senior members must give a school / Senior Citizen ID; they get 15% / 20% off every plan.
- Renewing early stacks the new days after the current expiry; a lapsed membership restarts on the payment date. You cannot prepay more than 1 year ahead.
- Cash must cover the amount due; card and GCash payments are always exact.
- Only members with an active plan can check in or book classes; a member cannot check in twice in a day.
- Classes must run between 05:00 and 23:00; a trainer cannot teach two overlapping classes; a member cannot book two overlapping classes; bookings cannot exceed capacity, and capacity cannot drop below the current bookings.
- A trainer who still teaches classes cannot be deleted. You cannot delete your own account or the last administrator.
- Deleting a member removes their check-ins and bookings but keeps payments, so revenue reports stay correct.
- Passwords need 8+ characters with letters and numbers. Every important action is written to `data/activity.log`.

## Project structure (layered architecture)

```
src/gym
├── Main.java              Entry point: load → login screen → save
├── AppContext.java        Creates and wires all services (dependency injection)
├── DataSeeder.java        Sample data for the first run (goes through the real services)
├── model/                 Person → Member → Regular/Student/SeniorMember, Trainer, Plan, Payment, CheckIn, FitnessClass, User, enums
├── service/               Business rules: Auth, Member, Trainer, Membership, Attendance, Class, Report
├── storage/               GymData (in-memory database), FileStorage (CSV), ActivityLog (audit trail)
├── exception/             GymException → ValidationException, NotFoundException
├── util/                  Input, Validator, Table, Fmt, Csv, PasswordHasher
└── ui/                    Menu (abstract) + LoginScreen and one menu class per module
```

Each layer only talks to the layer below it: `ui → service → storage/model`.

## What each day contributed

| Days | Skill | Where it lives in the capstone |
|---|---|---|
| 1–3 | Arithmetic, conditions, cashier/change | `MembershipService.quote()` discount math, cash tendered and change on the receipt |
| 4, 25 | Login, registration, authentication | `AuthService`, `LoginScreen` (3 attempts, forced password change), `PasswordHasher` |
| 5–8 | Loops, `switch`, validation, scoring | Every `Input` prompt re-asks until valid; switch expressions in menus; attendance averages |
| 9–12 | Arrays, searching, CRUD, totals | Member/trainer/class CRUD, search by ID/name/mobile, revenue totals |
| 13–17 | Classes, encapsulation, `ArrayList`, collections | Private fields + getters, `Map`/`List`/`Set`/`EnumMap`/`TreeMap`, `LinkedHashSet` bookings |
| 18–20 | Inheritance, polymorphism, relationships | `Person → Member → StudentMember`; `getDiscountRate()` overrides; trainer ↔ class ↔ member links |
| 21–24, 26–27 | File I/O, dates, persistence | CSV files per entity, safe temp-file writes, `LocalDate`/`LocalTime`/`YearMonth`, report export, activity log |
| 28–29 | Large-scale OOP, full-system integration | Layered packages, services, dependency injection, role-based menus |

## OOP and Java concepts demonstrated

| Concept | Where |
|---|---|
| Encapsulation | All fields `private`; state changes go through methods like `Member.renew()`, `FitnessClass.addBooking()` |
| Inheritance | `Member`/`Trainer` extend `Person`; three member subclasses; every menu extends `Menu`; exception hierarchy |
| Abstraction | Abstract `Person`, `Member`, `Menu`; `Storable` interface |
| Polymorphism | `getDiscountRate()`, `getExtraLabel()`, `getRoleLabel()`; `FileStorage.write()` takes any `Collection<? extends Storable>` |
| Records | Immutable `Payment`, `CheckIn`, `Quote`, `Dashboard`, `Totals` |
| Enums with data | `Plan` (days + price), `MemberType`, `MembershipStatus`, `PaymentMethod`, `Role` |
| Generics & functional interfaces | `ReportService.totalsBy(Function, Supplier)`, `FileStorage.readInto(Function, Consumer)`, `Input.choose(E[])` |
| Streams & lambdas | Filtering, sorting with `Comparator` chains, grouping for reports |
| Design patterns | Template Method (`Menu.show()`), Factory (`Member.create()`), Service layer, Dependency Injection (`AppContext`) |
| Exception handling | Unchecked business exceptions caught once in `Menu.show()`; corrupted CSV lines skipped with a warning |
| Security basics | PBKDF2 + random salt, constant-time compare, same error for unknown user and wrong password, hidden password input in a real terminal |

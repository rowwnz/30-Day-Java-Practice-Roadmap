# School Management System (Java, Console, OOP)

A menu-driven school management system written in pure Java (no external libraries, Java 8+).
Manages students, teachers, courses, enrollment, grades (NU Grade Point scale), attendance and reports.
Data is saved to `sms_data.dat` and reloaded on the next run. The first run loads sample data.

## How to run

**IntelliJ / NetBeans / VS Code:** open the `SchoolManagementSystem` folder, mark `src` as the source root, run `sms.Main`.

**Command line (Windows):**
```
cd SchoolManagementSystem
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
java -cp out sms.Main
```

**Command line (macOS / Linux):**
```
cd SchoolManagementSystem
javac -d out $(find src -name "*.java")
java -cp out sms.Main
```

Delete `sms_data.dat` to reset to the sample data. Tip: at any "Student ID", "Teacher ID" or "Course code" prompt, type `?` to list the records.

## Features

| Module | What it does |
|---|---|
| Students | Register, list, search, view profile + GWA, update, change status (Active/Inactive/Graduated), delete |
| Teachers | Add, list, search, view profile + assigned courses, update, delete |
| Courses | Create, list, class roster, update, assign/unassign teacher, delete |
| Enrollment | Enroll, drop, view by student or course |
| Grades | Record a score, encode a component for a whole class, view grade sheet, view grading system |
| Attendance | Take whole-class attendance, mark one student, view a student's record and rate |
| Reports | Transcript (with GWA), class roster (grades + attendance), GWA ranking, dashboard |

### Business rules enforced
- Only active students can enroll; no double enrollment; cannot re-enroll in a passed course; course capacity is enforced.
- A student with active enrollments cannot be set Inactive/Graduated; records with history cannot be deleted.
- A teacher assigned to courses cannot be deleted; capacity cannot drop below the current enrollment count.
- Scores must be 0-100; dropped enrollments cannot be graded; an enrollment becomes Completed once every component has a score.
- Attendance requires an active enrollment and cannot be recorded for future dates.
- Emails must be valid and unique; contact numbers must be 7-13 digits.

### Grading
Final grade = weighted sum of the components in `model/GradeComponent.java`
(Quizzes 20%, Interim 20%, Midterm 30%, Final 30% — edit there to match your syllabus).
The percentage is rounded and mapped to the NU Grade Point scale in `model/GradeScale.java`.
GWA = units-weighted average of grade points over completed courses.

## Project structure (layered architecture)

```
src/sms
├── Main.java              Entry point: load → menu → save
├── AppContext.java        Creates and wires all services (dependency injection)
├── DataSeeder.java        Sample data for the first run
├── model/                 Entities and enums (Person, Student, Teacher, Course, Enrollment, GradeSheet, ...)
├── repository/            Repository<T> interface + generic InMemoryRepository<T>
├── service/               Business rules (PersonService<T>, StudentService, CourseService, EnrollmentService, ...)
├── storage/               DataStore + FileStorage (serialization)
├── exception/             SmsException hierarchy
├── util/                  InputHelper, Validator, TablePrinter, IdGenerator
└── ui/                    Menu (abstract) + one menu class per module
```

Each layer only talks to the layer below it: `ui → service → repository → model`.

## OOP concepts demonstrated

| Concept | Where |
|---|---|
| Encapsulation | All fields are `private`; access through getters/setters; validation lives in services |
| Inheritance | `Student`/`Teacher` extend `Person`; `StudentService`/`TeacherService` extend `PersonService<T>`; every menu extends `Menu`; every exception extends `SmsException` |
| Abstraction | Abstract classes `Person`, `PersonService<T>`, `Menu`; interfaces `Identifiable`, `Repository<T>` |
| Polymorphism | `getRole()` / `getProfileDetails()` overrides; `MainMenu` stores sub-menus as `Menu[]`; services depend on `Repository<T>` not a concrete class |
| Generics | `Repository<T extends Identifiable>`, `InMemoryRepository<T>`, `PersonService<T extends Person>`, `InputHelper.readEnum(Class<E>)` |
| Composition | `Enrollment` owns a `GradeSheet`; `DataStore` owns the repositories; services are composed of other services |
| Enums with behavior | `GradeScale.fromScore()`, `GradeComponent` weights, `AttendanceStatus.countsAsAttended()` |
| Design patterns | Template Method (`Person.getProfile()`, `Menu.show()`), Repository, Dependency Injection (`AppContext`) |
| Exception handling | Custom checked-at-boundary exception hierarchy caught once in `Menu.show()` |
| Collections & streams | `LinkedHashMap`, `EnumMap`, `List`, `Comparator` chains, `Optional`/`OptionalDouble`, lambdas |
| File I/O | Object serialization in `FileStorage` |

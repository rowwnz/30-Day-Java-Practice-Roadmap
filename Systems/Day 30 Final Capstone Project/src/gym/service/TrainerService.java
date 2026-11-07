package gym.service;

import gym.exception.NotFoundException;
import gym.model.FitnessClass;
import gym.model.Trainer;
import gym.storage.GymData;
import gym.util.Validator;

import java.util.Comparator;
import java.util.List;

public class TrainerService {
    private final GymData data;

    public TrainerService(GymData data) {
        this.data = data;
    }

    public Trainer add(String name, String phone, String email, String specialty) {
        validate(null, name, phone, email, specialty);
        Trainer trainer = new Trainer(data.nextId("T", 3), name, phone, email, specialty.trim());
        data.getTrainers().put(trainer.getId(), trainer);
        return trainer;
    }

    public void update(String id, String name, String phone, String email, String specialty) {
        Trainer trainer = find(id);
        validate(trainer.getId(), name, phone, email, specialty);
        trainer.setFullName(name);
        trainer.setPhone(phone);
        trainer.setEmail(email);
        trainer.setSpecialty(specialty.trim());
    }

    public Trainer delete(String id) {
        Trainer trainer = find(id);
        List<FitnessClass> assigned = classesOf(trainer.getId());
        Validator.require(assigned.isEmpty(), trainer.getFullName() + " still teaches " + assigned.size()
                + " class(es). Reassign or delete them first.");
        data.getTrainers().remove(trainer.getId());
        return trainer;
    }

    public Trainer find(String id) {
        Trainer trainer = data.getTrainers().get(id.trim().toUpperCase());
        if (trainer == null) throw new NotFoundException("No trainer with ID '" + id + "'.");
        return trainer;
    }

    public String nameOf(String id) {
        Trainer trainer = data.getTrainers().get(id);
        return trainer == null ? "(unassigned)" : trainer.getFullName();
    }

    public List<Trainer> list() {
        return data.getTrainers().values().stream().sorted(Comparator.comparing(Trainer::getId)).toList();
    }

    public List<FitnessClass> classesOf(String trainerId) {
        return data.getClasses().values().stream()
                .filter(c -> c.getTrainerId().equals(trainerId))
                .sorted(ClassService.BY_SCHEDULE)
                .toList();
    }

    private void validate(String selfId, String name, String phone, String email, String specialty) {
        Validator.require(Validator.isName(name), "Invalid name: use letters, spaces, periods, apostrophes or hyphens.");
        Validator.require(Validator.isMobile(phone), "Mobile number must be 11 digits starting with 09.");
        Validator.require(email.isEmpty() || Validator.isEmail(email), "Invalid email address.");
        Validator.require(!specialty.isBlank(), "Specialty is required.");
        for (Trainer other : data.getTrainers().values()) {
            if (!other.getId().equals(selfId)) {
                Validator.require(!other.getPhone().equals(phone), "Mobile number is already used by " + other + ".");
            }
        }
    }
}

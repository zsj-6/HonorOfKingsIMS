package model;

import model.enums.HeroType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Hero {
    private String id;
    private String name;
    private HeroType type;
    private int baseHp;
    private int baseAttack;
    private int baseDefense;
    private List<String> compatibleEquipmentIds;

    public Hero() {
        this.compatibleEquipmentIds = new ArrayList<>();
    }

    public Hero(String id, String name, HeroType type,
                int baseHp, int baseAttack, int baseDefense) {
        setId(id);
        setName(name);
        setType(type);
        setBaseHp(baseHp);
        setBaseAttack(baseAttack);
        setBaseDefense(baseDefense);
        this.compatibleEquipmentIds = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Hero ID cannot be null or blank.");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Hero name cannot be null or blank.");
        }
        this.name = name;
    }

    public HeroType getType() {
        return type;
    }

    public void setType(HeroType type) {
        if (type == null) {
            throw new IllegalArgumentException("Hero type cannot be null.");
        }
        this.type = type;
    }

    public int getBaseHp() {
        return baseHp;
    }

    public void setBaseHp(int baseHp) {
        if (baseHp < 0) {
            throw new IllegalArgumentException("Base HP cannot be negative.");
        }
        this.baseHp = baseHp;
    }

    public int getBaseAttack() {
        return baseAttack;
    }

    public void setBaseAttack(int baseAttack) {
        if (baseAttack < 0) {
            throw new IllegalArgumentException("Base attack cannot be negative.");
        }
        this.baseAttack = baseAttack;
    }

    public int getBaseDefense() {
        return baseDefense;
    }

    public void setBaseDefense(int baseDefense) {
        if (baseDefense < 0) {
            throw new IllegalArgumentException("Base defense cannot be negative.");
        }
        this.baseDefense = baseDefense;
    }

    public List<String> getCompatibleEquipmentIds() {
        return Collections.unmodifiableList(compatibleEquipmentIds);
    }

    public void setCompatibleEquipmentIds(List<String> equipmentIds) {
        this.compatibleEquipmentIds = equipmentIds != null ? equipmentIds : new ArrayList<>();
    }

    public void addCompatibleEquipment(String equipmentId) {
        if (equipmentId == null || equipmentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Equipment ID cannot be null or blank.");
        }
        if (!compatibleEquipmentIds.contains(equipmentId)) {
            compatibleEquipmentIds.add(equipmentId);
        }
    }

    public boolean removeCompatibleEquipment(String equipmentId) {
        return compatibleEquipmentIds.remove(equipmentId);
    }

    public boolean isCompatibleWith(String equipmentId) {
        return compatibleEquipmentIds.contains(equipmentId);
    }

    @Override
    public String toString() {
        return "Hero{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", baseHp=" + baseHp +
                ", baseAttack=" + baseAttack +
                ", baseDefense=" + baseDefense +
                ", compatibleEquipment=" + compatibleEquipmentIds.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hero hero = (Hero) o;
        return Objects.equals(id, hero.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

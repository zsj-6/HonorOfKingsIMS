package model;

import model.enums.EquipmentType;
import java.util.Objects;

public class Equipment {
    private String id;
    private String name;
    private EquipmentType type;
    private int attackBonus;
    private int defenseBonus;
    private int hpBonus;
    private int usageCount;
    private double averageRating;
    private int heroUsageCount;

    public Equipment() {
    }

    public Equipment(String id, String name, EquipmentType type,
                     int attackBonus, int defenseBonus, int hpBonus) {
        setId(id);
        setName(name);
        setType(type);
        setAttackBonus(attackBonus);
        setDefenseBonus(defenseBonus);
        setHpBonus(hpBonus);
        this.usageCount = 0;
        setAverageRating(0.0);
        this.heroUsageCount = 0;
    }

    public Equipment(String id, String name, EquipmentType type,
                     int attackBonus, int defenseBonus, int hpBonus,
                     int usageCount, double averageRating, int heroUsageCount) {
        setId(id);
        setName(name);
        setType(type);
        setAttackBonus(attackBonus);
        setDefenseBonus(defenseBonus);
        setHpBonus(hpBonus);
        setUsageCount(usageCount);
        setAverageRating(averageRating);
        setHeroUsageCount(heroUsageCount);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Equipment ID cannot be null or blank.");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Equipment name cannot be null or blank.");
        }
        this.name = name;
    }

    public EquipmentType getType() {
        return type;
    }

    public void setType(EquipmentType type) {
        if (type == null) {
            throw new IllegalArgumentException("Equipment type cannot be null.");
        }
        this.type = type;
    }

    public int getAttackBonus() {
        return attackBonus;
    }

    public void setAttackBonus(int attackBonus) {
        if (attackBonus < 0) {
            throw new IllegalArgumentException("Attack bonus cannot be negative.");
        }
        this.attackBonus = attackBonus;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }

    public void setDefenseBonus(int defenseBonus) {
        if (defenseBonus < 0) {
            throw new IllegalArgumentException("Defense bonus cannot be negative.");
        }
        this.defenseBonus = defenseBonus;
    }

    public int getHpBonus() {
        return hpBonus;
    }

    public void setHpBonus(int hpBonus) {
        if (hpBonus < 0) {
            throw new IllegalArgumentException("HP bonus cannot be negative.");
        }
        this.hpBonus = hpBonus;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(int usageCount) {
        if (usageCount < 0) {
            throw new IllegalArgumentException("Usage count cannot be negative.");
        }
        this.usageCount = usageCount;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        if (averageRating < 0.0 || averageRating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 0.0 and 5.0.");
        }
        this.averageRating = averageRating;
    }

    public int getHeroUsageCount() {
        return heroUsageCount;
    }

    public void setHeroUsageCount(int heroUsageCount) {
        if (heroUsageCount < 0) {
            throw new IllegalArgumentException("Hero usage count cannot be negative.");
        }
        this.heroUsageCount = heroUsageCount;
    }

    public double getEquipmentScore() {
        return usageCount * 0.5 + averageRating * 0.3 + heroUsageCount * 0.2;
    }

    public void incrementUsage() {
        this.usageCount++;
    }

    @Override
    public String toString() {
        return "Equipment{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", attackBonus=" + attackBonus +
                ", defenseBonus=" + defenseBonus +
                ", hpBonus=" + hpBonus +
                ", usageCount=" + usageCount +
                ", averageRating=" + averageRating +
                ", heroUsageCount=" + heroUsageCount +
                ", score=" + String.format("%.2f", getEquipmentScore()) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equipment equipment = (Equipment) o;
        return Objects.equals(id, equipment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

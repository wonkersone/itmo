package ru.itmo.objects;

import ru.itmo.enums.Locations;

public abstract class GeneralObject {
    final String name;
    Locations location;

    public GeneralObject(String name, Locations location) {
        this.name = name;
        this.location = location;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        GeneralObject that = (GeneralObject) obj;
        return name.equals(that.name) && location.equals(that.location);
    }

    @Override
    public int hashCode() {
        return name.hashCode() + location.hashCode();
    }

    @Override
    public String toString() {
        return this.name + " находится " + this.location.getValue();
    }

    public Locations getLocation() {
        return location;
    }

    public void setLocation(Locations location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }
}

package ru.itmo.objects;

import ru.itmo.enums.Conditions;
import ru.itmo.enums.Locations;
import ru.itmo.exeptions.InvalidLocationException;
import ru.itmo.interfaces.Movable;
import ru.itmo.interfaces.Scenario;
import ru.itmo.records.Inanimate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Entity extends GeneralObject implements Movable, Scenario {
    Conditions condition;

    public Entity(String name, Locations location, Conditions condition) {
        super(name, location);
        this.condition = condition;
        System.out.println(name + " находится " + location.getValue() + " и " + condition.getValue());
    }


    public void go(Locations newLocation) {
        System.out.println(this.getName() + " побежал из " + this.getLocation().getValue() + " в " + newLocation.getValue());
        this.setLocation(newLocation);
    }

    @Override
    public void go(Inanimate loc) throws InvalidLocationException {
        if (loc == null) {
            throw new InvalidLocationException(this.getName() + " не может зайти сюда. Тут идет ремонт.");
        }
        if (this.condition == Conditions.CALM) {
            System.out.println(this.getName() + " плавно и элегантно вышел в " + loc.location().getValue() + " и прошел на балкон.");
        } else {
            System.out.println(this.getName() + " пнул " + loc.location().getValue() + " и прошел через нее на балкон.");
        }

        this.setLocation(Locations.BALCONY);
    }

    public void wander() {
        if (this.condition == Conditions.CALM) {
            System.out.println(this.getName() + " спокойно прогулялся по комнатам.");
        } else {
            System.out.println(this.getName() + " на нервяке прошелся по комнатам.");
        }
    }

    @Override
    public void run(Inanimate obj) {
        System.out.println(this.getName() + " выбежал за " + obj.name());

    }

    public void run(Environment area, Conditions condition) {
        System.out.println(this.getName() + " " + condition.getValue() + " бежит в " + area.getName());
    }

    @Override
    public void wakeUp() {
        System.out.println(this.getName() + " наконец проснулся.");
        if (Math.random() <= 0.5) {
            this.setCondition(Conditions.AGGRESSIVE);
        } else {
            this.setCondition(Conditions.CALM);
        }

    }

    @Override
    public void breathe() {
        System.out.println(this.getName() + " подышал свежим воздухом.");
        this.setCondition(Conditions.RELAXED);
    }

    @Override
    public void see(Inanimate object) {
        System.out.println(this.getName() + " увидел " + object.location().getValue());
    }

    @Override
    public void closeEyes() {
        System.out.println(this.getName() + " " + Conditions.SCARED.getValue() + " закрыл глаза и побежал");
        this.setCondition(Conditions.AMAZED);
    }


    public void hide(Buildings building) {
        System.out.println(this.getName() + " спрятался в " + building.getName());
    }

    public void setCondition(Conditions condition) {
        this.condition = condition;
        System.out.println();
        System.out.println("- - - - - - - - - - - - - - - -");
        System.out.println("Настроение " + this.getName() + " - " + condition.getValue());
        System.out.println("- - - - - - - - - - - - - - - -");
        System.out.println();
    }

    public Conditions getCondition() {
        return this.condition;
    }

    @Override
    public void escapeChoice(ArrayList<Inanimate> rooms) {
        System.out.println("Подсказка: чтобы выбраться из замка " + this.getName() + " должен найти ворота.");
        while (true) {
            List<Inanimate> availableRooms = new ArrayList<>(rooms);
            availableRooms.removeIf(room -> room.location().equals(this.getLocation()));

            Random random = new Random();
            Inanimate room1 = availableRooms.get(random.nextInt(availableRooms.size()));
            Inanimate room2;

            do {
                room2 = availableRooms.get(random.nextInt(availableRooms.size()));
            } while (room2.equals(room1));

            System.out.println("Выберите, в каком направлении побежит " + this.getName() + ":");
            System.out.println("1. " + room1.name());
            System.out.println("2. " + room2.name());

            Scanner scanner = new Scanner(System.in);
            int choice = 0;
            boolean validInput = false;

            while (!validInput) {
                try {
                    System.out.println("v ");
                    choice = Integer.parseInt(scanner.nextLine());
                    if (choice == 1 || choice == 2) {
                        validInput = true;
                    } else {
                        System.out.println("Введите 1 или 2, чтобы сделать выбор:");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Некорректный ввод. Введите число 1 или 2.");
                }
            }
            if (choice == 1) {
                this.go(room1.location());
                if (room1.location() == Locations.GATES) {
                    System.out.println(this.getName() + " выбежал из замка через ворота.");
                    this.setCondition(Conditions.TIRED);
                    break;
                }
            } else {
                this.go(room2.location());
                if (room2.location() == Locations.GATES) {
                    System.out.println(this.getName() + " выбежал из замка через ворота.");
                    this.setCondition(Conditions.TIRED);
                    break;
                }
            }
            System.out.println();
        }
    }

    @Override
    public void hideChoice(Buildings build1, Buildings build2, Buildings build3) {
        System.out.println("Выберите, где спрячется " + this.getName() + ": ");
        System.out.println("1 - в " + build1.getName() + ". Характеристики укрытия: Скрытность - " + (build1.getHideLevel() * 100) + "%, запах - " + build1.getSmell());
        System.out.println("2 - в " + build2.getName() + ". Характеристики укрытия: Скрытность - " + (build2.getHideLevel() * 100) + "%, запах - " + build2.getSmell());
        System.out.println("3 - в " + build3.getName() + ". Характеристики укрытия: Скрытность - " + (build3.getHideLevel() * 100) + "%, запах - " + build3.getSmell());

        Scanner scanner2 = new Scanner(System.in);
        int choice = 0;
        boolean validInput = false;

        while (!validInput) {
            try {
                System.out.println("v ");
                choice = Integer.parseInt(scanner2.nextLine());
                if (choice == 1 || choice == 2 || choice == 3) {
                    validInput = true;
                } else {
                    System.out.println("Введите число 1, 2 или 3, чтобы сделать выбор.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ввод. Введите число 1, 2 или 3.");
            }
        }
        Random random = new Random();
        if (choice == 1) {
            this.hide(build1);
            this.setCondition(Conditions.AGGRESSIVE);
            float chance = random.nextFloat();
            if (chance > build1.getHideLevel()) {
                System.out.println(this.getName() + " просидел в навозе до полуночи, и когда, казалось бы, восставшие жители королевства отступили, вылез из нее");
                System.out.println("Но чуть позже его выследили по запаху...");
                System.out.println();
                System.out.println("Вы проиграли(");
            } else {
                System.out.println(this.getName() + " просидел в навозе до полуночи и, дождавшись, пока восставшие жители королевства уйдут, вылез из нее");
                System.out.println("Чтобы сбить всех со следа, " + this.getName() + " смыл с себя запах в реке и успешно сбежал");
                System.out.println();
                System.out.println("Вы выиграли!!!");
            }
        } else if (choice == 2) {
            this.hide(build2);
            this.setCondition(Conditions.CALM);
            float chance = random.nextFloat();
            if (chance > build2.getHideLevel()) {
                System.out.println("На удивление восставшие жители королевства не заметили домик. " + this.getName() + " просидел в нем до полуночи и, ");
                System.out.println("дождавшись, пока повстанцы уйдут, успешно сбежал.");
                System.out.println();
                System.out.println("Вы выиграли!");
            } else {
                System.out.println("восставшие жители королевства почти сразу увидели домик на дереве и, прислушавшись, услышали шорох, исходящий оттуда");
                System.out.println("У " + this.getName() + " не удалось спрятаться...");
                System.out.println();
                System.out.println("Вы проиграли(");
            }
        } else {
            this.hide(build3);
            this.setCondition(Conditions.RELAXED);
            float chance = random.nextFloat();
            if (chance > build3.getHideLevel()) {
                System.out.println("Из-за своей неосторожности " + this.getName() + " оставил след, ведущий к бункеру, и его нашли...");
                System.out.println();
                System.out.println("Вы проиграли(");
            } else {
                System.out.println("Бункер почти невозможно заметить и тем более попасть туда");
                System.out.println(this.getName() + " просидел там несколько дней, подождав, пока его след будет утерян, и успешно сбежал");
                System.out.println();
                System.out.println("Вы выиграли!");
            }
        }
    }

    public void findCoin() {
        float rand = (float) Math.random();
        if (rand < 0.33) {
            System.out.println("По дороге " + this.getName() + " нашел монетку!");
            this.setCondition(Conditions.HAPPY);
        } else if (rand >= 0.33 && rand <= 0.66) {
            System.out.println("По дороге " + this.getName() + " увидел монетку, но не смог дотянуться до нее...");
            this.setCondition(Conditions.SAD);
        } else {
            System.out.println("На дороге валялась монетка, но " + this.getName() + " не заметил её и продолжил бежать.");
            this.setCondition(Conditions.SCARED);
        }
    }
}

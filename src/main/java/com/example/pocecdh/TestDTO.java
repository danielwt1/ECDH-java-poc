package com.example.pocecdh;

public class TestDTO {
    private String name;
    private String age;
    private String lastname;

    public TestDTO(String name, String age, String lastname) {
        this.name = name;
        this.age = age;
        this.lastname = lastname;
    }

    public TestDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public String toString() {
        return "TestDTO{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", lastname='" + lastname + '\'' +
                '}';
    }
}

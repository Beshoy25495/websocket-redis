package com.bwagih.websocket.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.Objects;


@RedisHash("Person")
public class Person implements Serializable {

    @Id
    private String id;

    private String firstName;

    private String lastName;

    private Integer age;
    private Address address;

    //Used to speed up the property based search
    @Indexed
    private int redisExtId;

    public Person() {}

    public Person(String id , String firstName, String lastName, int age) {
        this(id , firstName, lastName, age, null);
    }

    public Person(String id ,String firstName, String lastName, int age, Address address) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public int getRedisExtId() {
        return redisExtId;
    }

    public void setRedisExtId(int redisExtId) {
        this.redisExtId = redisExtId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return id == person.id && firstName.equals(person.firstName) && lastName.equals(person.lastName) && age.equals(person.age) && address.equals(person.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, age, address);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", address=" + address +
                '}';
    }
}

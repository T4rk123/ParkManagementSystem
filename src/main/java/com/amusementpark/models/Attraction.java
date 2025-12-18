package com.amusementpark.models;

import javax.persistence.*;

@Entity
@Table(name = "attractions")
public class Attraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "price")
    private Double price;

    @Column(name = "waiting_time")
    private Integer waitingTime;

    private Integer capacity;


    public Attraction() {}

    public Attraction(String name, String description, Double price, Integer waitingTime, Integer capacity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.waitingTime = waitingTime;
        this.capacity = capacity;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getWaitingTime() { return waitingTime; }
    public void setWaitingTime(Integer waitingTime) { this.waitingTime = waitingTime; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
}
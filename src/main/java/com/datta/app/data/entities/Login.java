package com.datta.app.data.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "logins")
@Getter
@Setter
@ToString
public class Login {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @OneToOne
    @JoinColumn(name = "user")
    public User user;

    @Column(name = "password")
    public String password;
}

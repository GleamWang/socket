package com.gleam.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    private String userid;
    private String password;

    public User(String userid, String password) {
        this.userid = userid;
        this.password = password;
    }
}

package com.consilium.vcg.vo;


import java.security.Principal;

public final class UserVo implements Principal {

    private final String name;


    public UserVo(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
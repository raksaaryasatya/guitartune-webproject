package com.guitartune.project_raksa.services;

import com.guitartune.project_raksa.models.User;


public interface GetAuthorities{
    public User getAuthenticatedUser() throws Exception;
}

package com.opinta.dao;

import com.opinta.entity.User;
import java.util.UUID;

public interface UserDao {

    User getByToken(UUID token);
}

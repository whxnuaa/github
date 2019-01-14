package com.jit.skiad.serviceinterface;

import com.jit.skiad.commons.ObjectRestResponse;
import com.jit.skiad.dto.UserDTO;

public interface AuthService {
    ObjectRestResponse<Void> register(UserDTO userToAdd);
    ObjectRestResponse<UserDTO> login(String username, String password);
    ObjectRestResponse<String> refresh(String oldToken);
    ObjectRestResponse<Void> forgetPassWord(String username,String number);
}

package com.viewscenes.netsupervisor.service;

import com.viewscenes.netsupervisor.entity.InfoUser;

import java.util.List;
import java.util.Map;

public interface InfoUserService {
    List<InfoUser> insertInfoUser(InfoUser infoUser);

    InfoUser getInfoUserById(String id);

    void deleteInfoUserById(String id);

    String getNameById(String id);

    Map<String, InfoUser> getAllUser();
}

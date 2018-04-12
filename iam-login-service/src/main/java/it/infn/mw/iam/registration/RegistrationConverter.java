/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2018
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.infn.mw.iam.registration;

import org.springframework.stereotype.Service;

import it.infn.mw.iam.core.IamRegistrationRequestStatus;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamRegistrationRequest;
import it.infn.mw.iam.persistence.model.IamUserInfo;

@Service
public class RegistrationConverter {

  public RegistrationRequestDto fromEntity(final IamRegistrationRequest request) {

    RegistrationRequestDto item = new RegistrationRequestDto();
    item.setUuid(request.getUuid());
    item.setCreationTime(request.getCreationTime());
    item.setStatus(request.getStatus().name());
    item.setLastUpdateTime(request.getLastUpdateTime());
    item.setUsername(request.getAccount().getUsername());
    item.setGivenname(request.getAccount().getUserInfo().getGivenName());
    item.setFamilyname(request.getAccount().getUserInfo().getFamilyName());
    item.setEmail(request.getAccount().getUserInfo().getEmail());
    item.setAccountId(request.getAccount().getUuid());
    item.setNotes(request.getNotes());

    return item;
  }

  public IamRegistrationRequest toEntity(final RegistrationRequestDto request) {

    IamUserInfo userInfo = new IamUserInfo();
    userInfo.setFamilyName(request.getFamilyname());
    userInfo.setGivenName(request.getGivenname());
    userInfo.setEmail(request.getEmail());
    userInfo.setBirthdate(request.getBirthdate());

    IamAccount account = new IamAccount();
    account.setUsername(request.getUsername());
    account.setUserInfo(userInfo);
    account.setUuid(request.getAccountId());

    IamRegistrationRequest entity = new IamRegistrationRequest();
    entity.setUuid(request.getUuid());
    entity.setCreationTime(request.getCreationTime());
    entity.setLastUpdateTime(entity.getLastUpdateTime());
    entity.setStatus(IamRegistrationRequestStatus.valueOf(request.getStatus()));
    entity.setAccount(account);
    entity.setNotes(request.getNotes());

    return entity;
  }

}

package it.infn.mw.iam.api.scim.provisioning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.scim.converter.UserConverter;
import it.infn.mw.iam.api.scim.exception.IllegalArgumentException;
import it.infn.mw.iam.api.scim.exception.ResourceNotFoundException;
import it.infn.mw.iam.api.scim.model.ScimListResponse;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.provisioning.paging.OffsetPageable;
import it.infn.mw.iam.api.scim.provisioning.paging.ScimPageRequest;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamUserInfo;
import it.infn.mw.iam.persistence.repository.IamAccountRespository;
import it.infn.mw.iam.persistence.repository.IamAuthoritiesRepository;

@Service
public class ScimUserProvisioning implements ScimProvisioning<ScimUser> {

  private final UserConverter converter;

  private final IamAccountRespository accountRepository;

  private final IamAuthoritiesRepository authorityRepository;

  @Autowired
  public ScimUserProvisioning(UserConverter converter,
    IamAccountRespository accountRepo, IamAuthoritiesRepository authorityRepo) {
    this.converter = converter;
    this.accountRepository = accountRepo;
    this.authorityRepository = authorityRepo;

  }

  private void idSanityChecks(String id) {

    if (id == null) {
      throw new IllegalArgumentException("id cannot be null");
    }

    if (id.trim()
      .isEmpty()) {
      throw new IllegalArgumentException("id cannot be the empty string");
    }
  }

  @Override
  public ScimUser getById(String id) {

    idSanityChecks(id);

    IamAccount account = accountRepository.findByUuid(id)
      .orElseThrow(() -> new ResourceNotFoundException(
        "No user mapped to id '" + id + "'"));

    return converter.toScim(account);

  }

  @Override
  public void delete(String id) {

    idSanityChecks(id);
    
    IamAccount account = accountRepository.findByUuid(id)
      .orElseThrow(() -> new ResourceNotFoundException(
        "No user mapped to id '" + id + "'"));

    accountRepository.delete(account);

  }

  @Override
  public ScimUser create(ScimUser user) {

    IamAccount account = new IamAccount();

    Date creationTime = new Date();
    String uuid = UUID.randomUUID()
      .toString();

    account.setUuid(uuid);
    account.setCreationTime(creationTime);
    account.setLastUpdateTime(creationTime);
    account.setUsername(user.getUserName());
    account.setActive(true);

    authorityRepository.findByAuthority("ROLE_USER")
      .map(a -> account.getAuthorities()
        .add(a))
      .orElseThrow(() -> new IllegalStateException(
        "ROLE_USER not found in database. This is a bug"));

    IamUserInfo userInfo = new IamUserInfo();
    userInfo.setGivenName(user.getName()
      .getGivenName());
    userInfo.setFamilyName(user.getName()
      .getFamilyName());

    userInfo.setEmail(user.getEmails()
      .get(0)
      .getValue());
    account.setUserInfo(userInfo);

    accountRepository.save(account);

    return converter.toScim(account);
  }

  @Override
  public ScimListResponse<ScimUser> list(ScimPageRequest params) {

    if (params.getCount() == 0) {
      int userCount = accountRepository.countAllUsers();
      return new ScimListResponse<>(Collections.emptyList(), userCount, 0, 1);
    }

    OffsetPageable op = new OffsetPageable(params.getStartIndex(),
      params.getCount());

    Page<IamAccount> results = accountRepository.findAll(op);

    List<ScimUser> resources = new ArrayList<>();

    results.getContent()
      .forEach(a -> resources.add(converter.toScim(a)));

    return new ScimListResponse<>(resources, results.getTotalElements(),
      resources.size(), op.getOffset() + 1);
  }

  @Override
  public ScimUser replace(String id, ScimUser scimItemToBeUpdated) {

    Optional<IamAccount> existingAccount = accountRepository.findByUuid(id);

    if (!existingAccount.isPresent()) {
      throw new ResourceNotFoundException("No user mapped to id '" + id + "'");
    }

    if (accountRepository.findByUsernameWithDifferentId(
      scimItemToBeUpdated.getUserName(), scimItemToBeUpdated.getId())
      .isPresent()) {
      throw new IllegalArgumentException(
        "userName is already mappped to another user");
    }

    IamAccount updatedAccount = converter.fromScim(scimItemToBeUpdated);
    updatedAccount.setId(existingAccount.get()
      .getId());

    accountRepository.save(updatedAccount);
    return converter.toScim(updatedAccount);

  }

}